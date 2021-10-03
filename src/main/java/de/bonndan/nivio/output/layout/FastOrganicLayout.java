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

    private final List<LayoutedComponent> nodes;

    /**
     * The force constant by which the attractive forces are divided and the
     * repulsive forces are multiple by the square of. The value equates to the
     * average radius there is of free space around each node. Default is 50.
     */
    private final double forceConstant;

    /**
     * Minimal distance limit.  Prevents of dividing by zero.
     */
    final double minDistanceLimit;

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
    double forceConstantSquared = 0;

    /**
     * Cached version of <minDistanceLimit> squared.
     */
    double minDistanceLimitSquared = 0;

    /**
     * Temperature to limit displacement at later stages of layout.
     */
    double temperature = 0;

    /**
     * Current iteration count.
     */
    int iteration = 0;

    /**
     * An array of locally stored  co-ordinate displacements for the vertices.
     */
    double[][] disp;

    /**
     * An array of locally stored co-ordinate positions for the vertices.
     */
    double[][] centerLocations;

    /**
     * The approximate radius of each cell, nodes only.
     */
    double[] radius;

    /**
     * The approximate radius squared of each cell, nodes only.
     */
    double[] radiusSquared;

    /**
     * Local copy of cell neighbours.
     */
    int[][] neighbours;

    /**
     * Maps from vertices to indices.
     */
    HashMap<LayoutedComponent, Integer> indices = new HashMap<>();

    private boolean debug;

    boolean minDistanceShortfall = false;

    /**
     * Constructs a new fast organic layout.
     */
    public FastOrganicLayout(@NonNull final List<LayoutedComponent> nodes,
                             double forceConstant,
                             double minDistanceLimit,
                             double maxDistanceLimit,
                             double initialTemp,
                             @Nullable final LandscapeConfig.LayoutConfig config
    ) {
        this.nodes = Objects.requireNonNull(nodes);

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
    void reduceTemperature() {
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

        setup();

        // Main iteration loop
        while (temperature > 1) {

            // Calculate attractive forces through edges
            calcAttraction();

            // Calculate repulsive forces on all vertices
            calcRepulsion();

            calcPositions();
            reduceTemperature();
            iteration++;
        }

        for (int i = 0; i < nodes.size(); i++) {
            LayoutedComponent vertex = nodes.get(i);
            vertex.setX((long) centerLocations[i][0]);
            vertex.setY((long) centerLocations[i][1]);
        }
    }

    void setup() {
        int n = nodes.size();

        disp = new double[n][];
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

        for (int i = 0; i < nodes.size(); i++) {
            LayoutedComponent layoutedComponent = this.nodes.get(i);
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

            disp[i] = new double[2];
            disp[i][0] = 0;
            disp[i][1] = 0;

            radius[i] = Math.max(halfWidth, halfHeight);
            radiusSquared[i] = radius[i] * radius[i];
        }

        // Moves cell location back to top-left from center locations used in
        // algorithm, resetting the edge points is part of the transaction

        InitialPlacementStrategy initialPlacementStrategy = new InitialPlacementStrategy(this.nodes);
        for (int i = 0; i < n; i++) {
            Point2D.Double start = initialPlacementStrategy.place(i);
            centerLocations[i][0] = start.x;
            centerLocations[i][1] = start.y;

            // Get lists of neighbours to all vertices, translate the cells
            // obtained in indices into vertexArray and store as an array
            // against the original cell index
            Component component = this.nodes.get(i).getComponent();
            List<Component> opposites = this.nodes.get(i).getOpposites();

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
    }

    private LayoutedComponent getBoundsForComponents(Component component) {
        return nodes.stream().filter(bounds1 -> bounds1.getComponent().equals(component)).findFirst().orElse(null);
    }

    /**
     * Takes the displacements calculated for each cell and applies them to the
     * local cache of cell positions. Limits the displacement to the current
     * temperature.
     */
    void calcPositions() {
        for (int index = 0; index < nodes.size(); index++) {
            // Get the distance of displacement for this node for this
            // iteration
            double deltaLength = Math.sqrt(disp[index][0] * disp[index][0] + disp[index][1] * disp[index][1]);

            if (deltaLength < 0.001) {
                deltaLength = 0.001;
            }

            // Scale down by the current temperature if less than the
            // displacement distance
            var factor = Math.min(deltaLength, temperature);
            double newXDisp = disp[index][0] / deltaLength * factor;
            double newYDisp = disp[index][1] / deltaLength * factor;

            // reset displacements
            disp[index][0] = 0;
            disp[index][1] = 0;

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
    void calcAttraction() {

        for (int i = 0; i < nodes.size(); i++) {
            for (int k = 0; k < neighbours[i].length; k++) {
                // Get the index of the other cell in the vertex array
                int j = neighbours[i][k];

                if (i == j) {
                    continue;
                }

                Point2D.Double displacement = getAttractionDisplacement(i, j);

                disp[i][0] -= displacement.x;
                disp[i][1] -= displacement.y;

                disp[j][0] += displacement.x;
                disp[j][1] += displacement.y;
            }
        }
    }

    Point2D.Double getAttractionDisplacement(int i, int j) {
        double xDelta = getDimDistanceBetween(i, j, 0);
        double yDelta = getDimDistanceBetween(i, j, 1);

        // The distance between the nodes
        double deltaLengthSquared = xDelta * xDelta + yDelta * yDelta - radiusSquared[i] - radiusSquared[j];

        if (deltaLengthSquared < minDistanceLimitSquared) {
            deltaLengthSquared = minDistanceLimitSquared;
        }

        double deltaLength = Math.sqrt(deltaLengthSquared);
        double force = (deltaLengthSquared) / forceConstant;

        double displacementX = (xDelta / deltaLength) * force;
        double displacementY = (yDelta / deltaLength) * force;


        //prevent collision
        var fxDelta = getDisplacedDistanceBetween(i, j, displacementX, 0);
        var fyDelta = getDisplacedDistanceBetween(i, j, displacementY, 1);

        if (Math.abs(fxDelta) < minDistanceLimit) {
            displacementX = displacementX - minDistanceLimit;
        }

        if (Math.abs(fyDelta) < minDistanceLimit) {
            displacementY = displacementY - minDistanceLimit;
        }

        return new Point2D.Double(displacementX, displacementY);
    }

    /**
     * Calculates the repulsive forces between all laid out nodes
     */
    void calcRepulsion() {
        int vertexCount = nodes.size();

        for (int i = 0; i < vertexCount; i++) {
            for (int j = i; j < vertexCount; j++) {

                if (j == i) {
                    continue;
                }

                Point2D.Double displacement = getRepulsionDisplacement(i, j);

                disp[i][0] += displacement.x;
                disp[i][1] += displacement.y;

                disp[j][0] -= displacement.x;
                disp[j][1] -= displacement.y;
            }
        }
    }

    Point2D.Double getRepulsionDisplacement(int i, int j) {
        double distance = getDistanceBetween(i, j);
        if (Math.abs(distance) > maxDistanceLimit) {
            // Ignore vertices too far apart
            return new Point2D.Double(0, 0);
        }

        double xDelta = getDimDistanceBetween(i, j, 0);
        double yDelta = getDimDistanceBetween(i, j, 1);

        double force = forceConstantSquared / distance;

        double displacementX = (xDelta / distance) * force;
        double displacementY = (yDelta / distance) * force;

        return new Point2D.Double(displacementX, displacementY);
    }


    /**
     * Calculates the distance between the borders (regarding radius).
     *
     * @param i index
     * @param j index
     */
    double getDistanceBetween(int i, int j) {
        return getDistanceBetween(
                centerLocations[i][0],
                centerLocations[j][0],
                centerLocations[i][1],
                centerLocations[j][1],
                radius[i],
                radius[j]);
    }

    /**
     * Calculates the distance between two nodes regarding the current displacement.
     *
     * @param i            index
     * @param j            index
     * @param displacement offset for the dimension, added to i, subtracted from j
     * @param dim          dimension
     */
    double getDisplacedDistanceBetween(int i, int j, double displacement, int dim) {
        double delta = centerLocations[i][dim] + displacement - centerLocations[j][dim] - displacement;

        if (delta == 0) {
            delta = 0.01;
        }

        double withRadius = Math.abs(delta) - radius[i] - radius[j];
        return delta < 0 ? withRadius * -1: withRadius;
    }

    /**
     * Calculates the distance between in one direction.
     *
     * @param i   index
     * @param j   index
     * @param dim x or y
     */
    double getDimDistanceBetween(int i, int j, int dim) {
        return getDisplacedDistanceBetween(i, j, 0, dim);
    }

    double getDistanceBetween(double x1, double x2, double y1, double y2, double radius1, double radius2) {
        double xDelta = x1 - x2;
        double yDelta = y1 - y2;

        if (xDelta == 0) {
            xDelta = 0.01;
        }

        if (yDelta == 0) {
            yDelta = 0.01;
        }

        return Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)) - radius1 - radius2;
    }

    public List<LayoutedComponent> getNodes() {
        return nodes;
    }

    public LayoutedComponent getOuterBounds(Component parent) {

        LayoutedComponent outer = new LayoutedComponent(parent, new ArrayList<>());
        var minX = new AtomicLong(0);
        var maxX = new AtomicLong(0);
        var minY = new AtomicLong(0);
        var maxY = new AtomicLong(0);

        for (LayoutedComponent b : this.nodes) {
            if (b.x < minX.get()) minX.set(b.x);
            if (b.x > maxX.get()) maxX.set(b.x);
            if (b.y < minY.get()) minY.set(b.y);
            if (b.y > maxY.get()) maxY.set(b.y);
        }

        outer.setWidth((double) maxX.get() - minX.get());
        outer.setHeight((double) maxY.get() - minY.get());
        outer.setChildren(nodes);

        return outer;
    }

    /**
     * Ensures that the given min distance is kept between all nodes.
     *
     * @throws IllegalStateException on shortfall
     */
    public void assertMinDistanceIsKept() {
        int vertexCount = nodes.size();

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
