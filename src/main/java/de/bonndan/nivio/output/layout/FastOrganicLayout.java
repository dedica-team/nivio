package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Fast organic layout algorithm.
 *
 * based on mxFastOrganicLayout from JGraphX by Gaudenz Alder Copyright (c) 2007
 */
public class FastOrganicLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastOrganicLayout.class);

    private final List<LayoutedComponent> bounds;

    /**
     * The force constant by which the attractive forces are divided and the
     * repulsive forces are multiple by the square of. The value equates to the
     * average radius there is of free space around each node. Default is 50.
     */
    private final double forceConstant;

    /**
     * Minimal distance limit.  Prevents of dividing by zero.
     */
    private final double minDistanceLimit;

    /**
     * The maximum distance between vertices, beyond which their
     * repulsion no longer has an effect
     */
    private final double maxDistanceLimit;

    /**
     * Start value of temperature. Default is 200.
     */
    private final double initialTemp;

    /**
     * Cache of <forceConstant>^2 for performance.
     */
    protected double forceConstantSquared = 0;

    /**
     * Cached version of <minDistanceLimit> squared.
     */
    protected double minDistanceLimitSquared = 0;

    /**
     * Temperature to limit displacement at later stages of layout.
     */
    protected double temperature = 0;

    /**
     * Current iteration count.
     */
    protected int iteration = 0;

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
     * Maps from vertices to indices.
     */
    protected HashMap<LayoutedComponent, Integer> indices = new HashMap<>();

    private boolean debug;

    private boolean minDistanceShortfall = false;

    /**
     * Constructs a new fast organic layout.
     */
    public FastOrganicLayout(@NonNull final List<LayoutedComponent> bounds,
                             double forceConstant,
                             double minDistanceLimit,
                             double maxDistanceLimit,
                             double initialTemp,
                             @Nullable final LandscapeConfig.LayoutConfig config
    ) {
        this.bounds = Objects.requireNonNull(bounds);

        if (forceConstant < 0.001) {
            forceConstant = 0.001;
        }
        Float forceFactor = Optional.ofNullable(config).map(LandscapeConfig.LayoutConfig::getForceConstantFactor).orElse(1f);
        this.forceConstant = forceConstant * forceFactor;

        Float minDisFactor = Optional.ofNullable(config).map(LandscapeConfig.LayoutConfig::getMinDistanceLimitFactor).orElse(1f);
        this.minDistanceLimit = minDistanceLimit * minDisFactor;

        Float maxDistFactor = Optional.ofNullable(config).map(LandscapeConfig.LayoutConfig::getMaxDistanceLimitFactor).orElse(1f);
        this.maxDistanceLimit = maxDistanceLimit * maxDistFactor;

        this.initialTemp = initialTemp;

    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Reduces the temperature of the layout from an initial setting in a linear
     * fashion to zero.
     */
    protected void reduceTemperature() {
        var factor = 0.8;

        if (temperature < 5 && !minDistanceShortfall) {
            temperature = 0;
        }
        if (minDistanceShortfall) {
            minDistanceShortfall = false;
            LOGGER.info("Compensating min distance shortfall, slower reducing temp {} in iteration {}.", temperature, iteration);
            factor = 0.97;
        }
        temperature = temperature * factor;
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
            double halfWidth = layoutedComponent.getWidth() / 2.0;
            double halfHeight = layoutedComponent.getHeight() / 2.0;
            double x = layoutedComponent.getX();
            double y = layoutedComponent.getY();

            centerLocations[i][0] = x + halfWidth;
            centerLocations[i][1] = y + halfHeight;

            radius[i] = Math.max(halfWidth, halfHeight);
            radiusSquared[i] = radius[i] * radius[i];
        }

        // Moves cell location back to top-left from center locations used in
        // algorithm, resetting the edge points is part of the transaction

        InitialPlacementStrategy initialPlacementStrategy = new InitialPlacementStrategy(this.bounds);
        for (int i = 0; i < n; i++) {
            Point2D.Double start = initialPlacementStrategy.place(i);
            centerLocations[i][0] = start.x;
            centerLocations[i][1] = start.y;

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
                    throw new IllegalStateException(String.format("Could not find neighbour for %s", component));
                }
            }
        }

        temperature = initialTemp;

        // Main iteration loop
        while (temperature > 1) {

            // Calculate repulsive forces on all vertices
            calcRepulsion();

            // Calculate attractive forces through edges
            calcAttraction();

            calcPositions();
            reduceTemperature();
            iteration++;
        }

        for (int i = 0; i < bounds.size(); i++) {
            LayoutedComponent vertex = bounds.get(i);
            vertex.setX((long) centerLocations[i][0]);
            vertex.setY((long) centerLocations[i][1]);
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
            var factor = Math.min(deltaLength, temperature);
            double newXDisp = dispX[index] / deltaLength * factor;
            double newYDisp = dispY[index] / deltaLength * factor;

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
                if (i == j) {
                    continue;
                }

                double distance = getDistanceBetween(i, j);
                double xDelta = getDistanceBetween(i, j, 0);
                double yDelta = getDistanceBetween(i, j, 1);

                //attraction is low at min distance limit
                var factor = Math.abs(distance) - minDistanceLimit*1.1;

                double force = factor * factor / forceConstant;

                double displacementX = (xDelta / distance) * force;
                double displacementY = (yDelta / distance) * force;

                this.dispX[i] -= displacementX;
                this.dispY[i] -= displacementY;

                this.dispX[j] += displacementX;
                this.dispY[j] += displacementY;
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

                if (j == i) {
                    continue;
                }

                double xDelta = getDistanceBetween(i, j, 0);
                double yDelta = getDistanceBetween(i, j, 1);

                double distance = getDistanceBetween(i, j);

                if (debug) {
                    //LOGGER.debug("Iteration {} temp {}: repulsion index {} {} deltaLengthWithRadius is {}", iteration, temperature, i, j, distance);
                }

                if (Math.abs(distance) > maxDistanceLimit) {
                    // Ignore vertices too far apart
                    continue;
                }

                if (Math.abs(distance) < minDistanceLimit) {
                    if (debug) {
                        LOGGER.debug("Iteration {} temp {}: repulsion index {} {} distance shortfall {}/{}", iteration, temperature, i, j, distance, minDistanceLimit);
                    }
                    minDistanceShortfall = true;
                }

                double force = forceConstantSquared / distance;

                double displacementX = (xDelta / distance) * force;
                double displacementY = (yDelta / distance) * force;

                dispX[i] += displacementX;
                dispY[i] += displacementY;

                dispX[j] -= displacementX;
                dispY[j] -= displacementY;
            }
        }
    }

    /**
     * Calculates the distance between in one direction.
     *
     * @param i   index
     * @param j   index
     * @param dim x or y
     */
    double getDistanceBetween(int i, int j, int dim) {
        double delta = centerLocations[i][dim] - centerLocations[j][dim];

        if (delta == 0) {
            delta = 0.01;
        }

        return delta - radius[i] - radius[j];
    }

    /**
     * Calculates the distance between the borders (regarding radius).
     *
     * @param i index
     * @param j index
     */
    double getDistanceBetween(int i, int j) {
        double xDelta = centerLocations[i][0] - centerLocations[j][0];
        double yDelta = centerLocations[i][1] - centerLocations[j][1];

        if (xDelta == 0) {
            xDelta = 0.01;
        }

        if (yDelta == 0) {
            yDelta = 0.01;
        }

        return Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)) - radius[i] - radius[j];
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
            if (b.x < minX.get()) minX.set(b.x);
            if (b.x > maxX.get()) maxX.set(b.x);
            if (b.y < minY.get()) minY.set(b.y);
            if (b.y > maxY.get()) maxY.set(b.y);
        }

        outer.setWidth(maxX.get() - minX.get());
        outer.setHeight(maxY.get() - minY.get());
        outer.setChildren(bounds);

        return outer;
    }

    public void checkDistances() {
        int vertexCount = bounds.size();

        for (int i = 0; i < vertexCount; i++) {
            for (int j = i; j < vertexCount; j++) {
                if (j == i) {
                    continue;
                }
                double deltaLengthWithRadius = getDistanceBetween(i, j);
                if (Math.abs(deltaLengthWithRadius) < minDistanceLimit * 0.9) {
                    throw new IllegalStateException(String.format("Min distance shortfall of %s/%s for i=%d j=%d", deltaLengthWithRadius, minDistanceLimit, i, j));
                }
            }
        }
    }
}
