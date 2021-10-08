package de.bonndan.nivio.output.layout;

import com.google.common.util.concurrent.AtomicDouble;
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
     * Minimal distance limit.  Prevents of dividing by zero.
     */
    final double minDistanceLimit;

    /**
     * The maximum distance between vertices, beyond which their
     * repulsion no longer has an effect
     */
    final double maxDistanceLimit;

    /**
     * Start value of temperature. Default is 200.
     */
    private final double initialTemp;

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
     * all distances (regarding radius).
     */
    double[][] distances;

    /**
     * The approximate radius of each cell, nodes only.
     */
    double[] radius;

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
                             double minDistanceLimit,
                             double maxDistanceLimit,
                             double initialTemp,
                             @Nullable final LandscapeConfig.LayoutConfig config
    ) {
        this.nodes = Objects.requireNonNull(nodes);

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
     *
     * This is important for layouts that do not find a stable equilibrium.
     */
    void reduceTemperature() {
        var factor = 0.8;

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
            calcStrongAttraction();

            // Calculate weak attractive forces
            calcWeakAttraction();

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
        distances = new double[n][n];
        neighbours = new int[n][];
        radius = new double[n];
        minDistanceLimitSquared = minDistanceLimit * minDistanceLimit;

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
                if (index == null) {
                    throw new IllegalStateException(String.format("Could not find neighbour for %s", component));
                }
                neighbours[i][j] = index;
            }
        }

        calcPositions();
        temperature = initialTemp;
    }

    private LayoutedComponent getBoundsForComponents(Component component) {
        return nodes.stream().filter(bounds1 -> bounds1.getComponent().equals(component)).findFirst().orElse(null);
    }

    /**
     * Takes the displacements calculated for each cell and applies them to the local cache of cell positions.
     */
    void calcPositions() {

        AtomicDouble maxMove = new AtomicDouble(0);

        final int vertexCount = nodes.size();
        var tempFactor = temperature / initialTemp;
        for (int index = 0; index < vertexCount; index++) {

            double newXDisp = disp[index][0] * tempFactor;
            if (Math.abs(newXDisp) > maxMove.get())
                maxMove.set(Math.abs(newXDisp));
            double newYDisp = disp[index][1] * tempFactor;
            if (Math.abs(newYDisp) > maxMove.get())
                maxMove.set(Math.abs(newYDisp));

            // reset displacements
            disp[index][0] = 0;
            disp[index][1] = 0;

            // Update the cached cell locations
            centerLocations[index][0] += newXDisp;
            centerLocations[index][1] += newYDisp;

            if (debug)
                LOGGER.debug("Moving {} by {} {} to {} {}", index, (int) newXDisp, (int) newYDisp, (int) centerLocations[index][0], (int) centerLocations[index][1]);
        }

        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                if (j == i) {
                    continue;
                }
                distances[i][j] = getDistanceBetween(i, j);
                if (Math.abs(distances[i][j]) < minDistanceLimit) {
                    if (debug) {
                        LOGGER.debug("Iteration {} temp {}: repulsion index {} {} distance shortfall {}/{}", iteration, (int) temperature, i, j, (int) distances[i][j], minDistanceLimit);
                    }
                    minDistanceShortfall = true;
                }
            }
        }

        if (iteration > 0 && maxMove.get() < 1) {
            LOGGER.debug("Iteration {}: No more significant movement, reducing temp to zero", iteration);
            temperature = 0;
        }
        if (debug)
            LOGGER.debug("Iteration {} temp {}: max move {}", iteration, (int) temperature, maxMove.get());

    }

    /**
     * Calculates the attractive forces between all laid out nodes linked by
     * edges
     */
    void calcStrongAttraction() {

        for (int i = 0; i < nodes.size(); i++) {
            for (int k = 0; k < neighbours[i].length; k++) {
                // Get the index of the other cell in the vertex array
                int index = neighbours[i][k];

                if (i == index) {
                    continue;
                }

                Point2D.Double displacement = getAttractionDisplacement(i, index);

                disp[i][0] += displacement.x;
                disp[i][1] += displacement.y;

                disp[index][0] -= displacement.x;
                disp[index][1] -= displacement.y;
            }
        }
    }

    /**
     * Cohesive forces
     */
    void calcWeakAttraction() {
        int vertexCount = nodes.size();

        for (int i = 0; i < vertexCount; i++) {
            for (int j = i; j < vertexCount; j++) {

                if (j == i) {
                    continue;
                }

                if (distances[i][j] < maxDistanceLimit)
                    continue;

                Point2D.Double displacement = getAttractionDisplacement(i, j);

                disp[i][0] += displacement.x * 0.1;
                disp[i][1] += displacement.y * 0.1;

                disp[j][0] -= displacement.x * 0.1;
                disp[j][1] -= displacement.y * 0.1;
            }
        }
    }

    /**
     * Calculates the distance (x and y) for displacements for each i/j.
     *
     * If i.x < j.x the dx is positive, so that i shifts towards j.
     *
     * Returns a fourth in each direction, since th displacement is applied to both nodes and the reverse attraction is calculated, too.
     */
    Point2D.Double getAttractionDisplacement(int i, int j) {

        double distance = distances[i][j];

        if (distance <= minDistanceLimit) {
            LOGGER.debug("Attraction {} {} distance {} is below min distance", i, j, (int) distance);
            return new Point2D.Double(0, 0);
        }

        final double force = (distance - minDistanceLimit) / distance;

        double xDelta = getDimDistanceBetweenCenters(i, j, 0);
        double xDeltaBetween = Math.abs(xDelta) - radius[i] - radius[j];
        double displacementX = 0;
        if (xDeltaBetween > 0) {
            displacementX = (xDeltaBetween / distance) * force * xDeltaBetween / 4;
        }

        double yDelta = getDimDistanceBetweenCenters(i, j, 1);
        double yDeltaBetween = Math.abs(yDelta) - radius[i] - radius[j];
        double displacementY = 0;
        if (xDeltaBetween > 0) {
            displacementY = (yDeltaBetween / distance) * force * yDeltaBetween / 4;
        }


        if (xDelta > 0)
            displacementX *= -1;
        if (yDelta > 0)
            displacementY *= -1;

        LOGGER.debug("Attraction {} {} distance {} resulting in force is {}: dx {} dy {}", i, j, (int) distance, (int) force, (int) displacementX, (int) displacementY);
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

    /**
     * Repulsion is stronger on short distances and limited by maxDistanceLimit
     *
     * Returns a fourth the value because if is applied to both nodes and reverse
     */
    Point2D.Double getRepulsionDisplacement(int i, int j) {
        double distance = distances[i][j];
        if (Math.abs(distance) > maxDistanceLimit) {
            // Ignore vertices too far apart
            return new Point2D.Double(0, 0);
        }

        var factor = (maxDistanceLimit - Math.abs(distance)) / maxDistanceLimit;
        if (distance < 0)
            factor *= -1;

        double force = maxDistanceLimit * factor;

        double xDelta = getDimDistanceBetweenCenters(i, j, 0);
        double yDelta = getDimDistanceBetweenCenters(i, j, 1);
        double displacementX = (xDelta / distance) * force / 4;
        double displacementY = (yDelta / distance) * force / 4;

        LOGGER.debug("Iteration {}: Repulsion {} {} distance {} resulting in force is {}: dx {} dy {}", iteration, i, j, (int) distance, (int) force, (int) displacementX, (int) displacementY);
        return new Point2D.Double(displacementX, displacementY);
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

    /**
     * Calculates the distance between in one direction.
     *
     * @param i   index
     * @param j   index
     * @param dim x or y
     * @return the distance in one direction, negative means j is greater
     */
    double getDimDistanceBetweenCenters(int i, int j, int dim) {
        double delta = centerLocations[i][dim] - centerLocations[j][dim];

        if (delta == 0) {
            delta = 0.001;
        }

        return delta;
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
