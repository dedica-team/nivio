package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fast organic layout algorithm.
 * <p>
 * based on mxFastOrganicLayout from JGraphX by Gaudenz Alder Copyright (c) 2007
 */
public class FastOrganicLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastOrganicLayout.class);

    private final List<LayoutedComponent> bounds;

    /**
     * The force constant by which the attractive forces are divided and the
     * replusive forces are multiple by the square of. The value equates to the
     * average radius there is of free space around each node. Default is 50.
     */
    protected double forceConstant = 50;

    /**
     * Cache of <forceConstant>^2 for performance.
     */
    protected double forceConstantSquared = 0;

    /**
     * Minimal distance limit. Default is 2. Prevents of
     * dividing by zero.
     */
    protected double minDistanceLimit = 2;

    /**
     * Cached version of <minDistanceLimit> squared.
     */
    protected double minDistanceLimitSquared = 0;

    /**
     * The maximum distance between vertices, beyond which their
     * repulsion no longer has an effect
     */
    protected double maxDistanceLimit = 300;

    /**
     * Start value of temperature. Default is 200.
     */
    protected double initialTemp = 200;

    /**
     * Temperature to limit displacement at later stages of layout.
     */
    protected double temperature = 0;

    /**
     * Total number of iterations to run the layout though.
     */
    protected double maxIterations = 0;

    /**
     * Current iteration count.
     */
    protected double iteration = 0;

    /**
     * An array of locally stored X co-ordinate displacements for the vertices.
     */
    protected double[] dispX;

    /**
     * An array of locally stored Y co-ordinate displacements for the vertices.
     */
    protected double[] dispY;

    /**
     * An array of locally stored co-ordinate positions for the vertices.
     */
    protected double[][] centerLocations;

    /**
     * The approximate radius of each cell, nodes only.
     */
    protected double[] radius;

    /**
     * The approximate radius squared of each cell, nodes only.
     */
    protected double[] radiusSquared;

    /**
     * Local copy of cell neighbours.
     */
    protected int[][] neighbours;

    /**
     * Boolean flag that specifies if the layout is allowed to run. If this is
     * set to false, then the layout exits in the following iteration.
     */
    protected boolean allowedToRun = true;

    /**
     * Maps from vertices to indices.
     */
    protected Hashtable<LayoutedComponent, Integer> indices = new Hashtable<>();
    private boolean debug;

    /**
     * Constructs a new fast organic layout.
     */
    public FastOrganicLayout(List<LayoutedComponent> bounds) {
        this.bounds = bounds;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setMaxIterations(double value) {
        maxIterations = value;
    }

    /**
     * Reduces the temperature of the layout from an initial setting in a linear
     * fashion to zero.
     */
    protected void reduceTemperature() {
        temperature = initialTemp * (1.0 - iteration / maxIterations);
    }

    public void execute() {

        int n = bounds.size();

        dispX = new double[n];
        dispY = new double[n];
        centerLocations = new double[n][];
        neighbours = new int[n][];
        radius = new double[n];
        radiusSquared = new double[n];

        minDistanceLimitSquared = minDistanceLimit * minDistanceLimit;

        if (forceConstant < 0.001) {
            forceConstant = 0.001;
        }

        forceConstantSquared = forceConstant * forceConstant;

        // Create a map of vertices first. This is required for the array of
        // arrays called neighbours which holds, for each vertex, a list of
        // ints which represents the neighbours cells to that vertex as
        // the indices into vertexArray

        for (int i = 0; i < bounds.size(); i++) {
            LayoutedComponent layoutedComponent = this.bounds.get(i);
            indices.put(layoutedComponent, i);
            centerLocations[i] = new double[2];


            // Set the X,Y value of the internal version of the cell to
            // the center point of the vertex for better positioning
            double width = layoutedComponent.getWidth();
            double height = layoutedComponent.getHeight();
            double x = layoutedComponent.getX();
            double y = layoutedComponent.getY();

            centerLocations[i][0] = x + width / 2.0;
            centerLocations[i][1] = y + height / 2.0;

            radius[i] = Math.max(width, height);
            radiusSquared[i] = radius[i] * radius[i];
        }

        // Moves cell location back to top-left from center locations used in
        // algorithm, resetting the edge points is part of the transaction

        InitialPlacementStrategy initialPlacementStrategy = new InitialPlacementStrategy(this.bounds);
        for (int i = 0; i < n; i++) {
            Point2D.Double start = initialPlacementStrategy.place(i);
            dispX[i] = start.x;
            dispY[i] = start.y;

            // Get lists of neighbours to all vertices, translate the cells
            // obtained in indices into vertexArray and store as an array
            // against the original cell index
            Component component = this.bounds.get(i).getComponent();
            List<Component> opposites = this.bounds.get(i).getOpposites();

            neighbours[i] = new int[opposites.size()];
            if (debug) {
                LOGGER.debug("Bounds {} has {} neighbours", component, opposites.size());
            }
            for (int j = 0; j < opposites.size(); j++) {
                Integer index = indices.get(getBoundsForComponents(opposites.get(j)));

                // Check the connected cell in part of the vertex list to be
                // acted on by this layout
                if (index != null) {
                    neighbours[i][j] = index;
                }

                // Else if index of the other cell doesn't correspond to
                // any cell listed to be acted upon in this layout. Set
                // the index to the value of this vertex (a dummy self-loop)
                // so the attraction force of the edge is not calculated
                else {
                    neighbours[i][j] = i;
                    throw new RuntimeException("Could not find neighbour for " + component);
                }
            }
        }

        temperature = initialTemp;

        // If max number of iterations has not been set, guess it
        if (maxIterations == 0) {
            maxIterations = 20.0 * Math.sqrt(n);
        }

        // Main iteration loop
        for (iteration = 0; iteration < maxIterations; iteration++) {
            if (!allowedToRun) {
                return;
            }

            // Calculate repulsive forces on all vertices
            calcRepulsion();

            // Calculate attractive forces through edges
            calcAttraction();

            calcPositions();
            reduceTemperature();
        }

        for (int i = 0; i < bounds.size(); i++) {
            LayoutedComponent vertex = bounds.get(i);
            vertex.setX(centerLocations[i][0]);
            vertex.setY(centerLocations[i][1]);
        }
    }

    private LayoutedComponent getBoundsForComponents(Component component) {
        return bounds.stream().filter(bounds1 -> bounds1.getComponent().equals(component)).findFirst().orElse(null);
    }

    /**
     * Takes the displacements calculated for each cell and applies them to the
     * local cache of cell positions. Limits the displacement to the current
     * temperature.
     */
    protected void calcPositions() {
        for (int index = 0; index < bounds.size(); index++) {
            // Get the distance of displacement for this node for this
            // iteration
            double deltaLength = Math.sqrt(dispX[index] * dispX[index] + dispY[index] * dispY[index]);

            if (deltaLength < 0.001) {
                deltaLength = 0.001;
            }

            // Scale down by the current temperature if less than the
            // displacement distance
            double newXDisp = dispX[index] / deltaLength * Math.min(deltaLength, temperature);
            double newYDisp = dispY[index] / deltaLength * Math.min(deltaLength, temperature);

            // reset displacements
            dispX[index] = 0;
            dispY[index] = 0;

            // Update the cached cell locations
            centerLocations[index][0] += newXDisp;
            centerLocations[index][1] += newYDisp;
            if (debug) {
                LOGGER.debug("Iteration {} temp {}: Shifting index {} center by dx {} and dy {}", iteration, temperature, index, newXDisp, newYDisp);
            }
        }
    }

    /**
     * Calculates the attractive forces between all laid out nodes linked by
     * edges
     */
    protected void calcAttraction() {
        // Check the neighbours of each vertex and calculate the attractive
        // force of the edge connecting them
        for (int i = 0; i < bounds.size(); i++) {
            for (int k = 0; k < neighbours[i].length; k++) {
                // Get the index of the othe cell in the vertex array
                int j = neighbours[i][k];

                // Do not proceed self-loops
                if (i != j) {
                    double xDelta = centerLocations[i][0] - centerLocations[j][0];
                    double yDelta = centerLocations[i][1] - centerLocations[j][1];

                    // The distance between the nodes
                    double deltaLengthSquared = xDelta * xDelta + yDelta * yDelta - radiusSquared[i] - radiusSquared[j];

                    if (deltaLengthSquared < minDistanceLimitSquared) {
                        deltaLengthSquared = minDistanceLimitSquared;
                    }

                    double deltaLength = Math.sqrt(deltaLengthSquared);
                    double force = (deltaLengthSquared) / forceConstant;

                    double displacementX = (xDelta / deltaLength) * force;
                    double displacementY = (yDelta / deltaLength) * force;

                    this.dispX[i] -= displacementX;
                    this.dispY[i] -= displacementY;

                    this.dispX[j] += displacementX;
                    this.dispY[j] += displacementY;
                }
            }
        }
    }

    /**
     * Calculates the repulsive forces between all laid out nodes
     */
    protected void calcRepulsion() {
        int vertexCount = bounds.size();

        for (int i = 0; i < vertexCount; i++) {
            for (int j = i; j < vertexCount; j++) {
                // Exits if the layout is no longer allowed to run
                if (!allowedToRun) {
                    return;
                }

                if (j != i) {
                    double xDelta = centerLocations[i][0] - centerLocations[j][0];
                    double yDelta = centerLocations[i][1] - centerLocations[j][1];

                    if (xDelta == 0) {
                        xDelta = 0.01;
                    }

                    if (yDelta == 0) {
                        yDelta = 0.01;
                    }

                    // Distance between nodes
                    double deltaLength = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));

                    double deltaLengthWithRadius = deltaLength - radius[i] - radius[j];

                    if (debug) {
                        LOGGER.debug("Iteration {} temp {}: repulsion index {} {} deltaLengthWithRadius is {}", iteration, temperature, i,j, deltaLengthWithRadius);
                    }

                    if (deltaLengthWithRadius > maxDistanceLimit) {
                        // Ignore vertices too far apart
                        continue;
                    }

                    if (deltaLengthWithRadius < minDistanceLimit) {
                        deltaLengthWithRadius = minDistanceLimit;
                    }

                    double force = forceConstantSquared / deltaLengthWithRadius;

                    double displacementX = (xDelta / deltaLength) * force;
                    double displacementY = (yDelta / deltaLength) * force;

                    dispX[i] += displacementX;
                    dispY[i] += displacementY;

                    dispX[j] -= displacementX;
                    dispY[j] -= displacementY;
                }
            }
        }
    }

    public List<LayoutedComponent> getBounds() {
        return bounds;
    }

    public LayoutedComponent getOuterBounds(Component parent) {

        LayoutedComponent outer = new LayoutedComponent(parent, new ArrayList<>());
        var minX = new AtomicLong(0);
        var maxX = new AtomicLong(0);
        var minY = new AtomicLong(0);
        var maxY = new AtomicLong(0);

        for (LayoutedComponent b : this.bounds) {
            if (b.x < minX.get()) minX.set((long) b.x);
            if (b.x > maxX.get()) maxX.set((long) b.x);
            if (b.y < minY.get()) minY.set((long) b.y);
            if (b.y > maxY.get()) maxY.set((long) b.y);
        }

        outer.setWidth(maxX.get() - minX.get());
        outer.setHeight(maxY.get() - minY.get());
        outer.setChildren(bounds);

        return outer;
    }

    public void setForceConstant(double forceConstant) {
        this.forceConstant = forceConstant;
    }

    public void setMaxDistanceLimit(double maxDistanceLimit) {
        this.maxDistanceLimit = maxDistanceLimit;
    }

    public void setMinDistanceLimit(double minDistanceLimit) {
        this.minDistanceLimit = minDistanceLimit;
    }

    public double getMinDistanceLimit() {
        return minDistanceLimit;
    }
}
