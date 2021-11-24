package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Fast organic layout algorithm.
 *
 * based on mxFastOrganicLayout from JGraphX by Gaudenz Alder Copyright (c) 2007
 */
public class FastOrganicLayout {

    final List<LayoutedComponent> nodes;
    private final LayoutLogger layoutLogger;
    private final Forces forces;

    /**
     * Start value of temperature. Default is 200.
     */
    protected double initialTemp;

    /**
     * Temperature to limit displacement at later stages of layout.
     */
    protected double temperature = 0;

    /**
     * Total number of iterations to run the layout though.
     */
    protected int maxIterations = 0;

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
    HashMap<LayoutedComponent, Integer> indices = new HashMap<>();

    private boolean debug;

    double[][] distances;

    /**
     * Constructs a new fast organic layout.
     */
    public FastOrganicLayout(@NonNull final List<LayoutedComponent> nodes,
                             @NonNull final Forces forces,
                             int initialTemp
    ) {
        this.nodes = Objects.requireNonNull(nodes);
        this.forces = Objects.requireNonNull(forces);
        this.initialTemp = initialTemp;
        this.layoutLogger = new LayoutLogger();

        // If max number of iterations has not been set, guess it
        if (maxIterations == 0) {
            maxIterations = (int) (20.0 * Math.sqrt(nodes.size()));
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Reduces the temperature of the layout from an initial setting in a linear
     * fashion to zero.
     */
    protected void reduceTemperature() {
        temperature = initialTemp * (1.0 - iteration / (double) maxIterations);
    }

    public void setup() {

        int n = nodes.size();
        dispX = new double[n];
        dispY = new double[n];
        centerLocations = new double[n][];
        distances = new double[n][n];
        neighbours = new int[n][];
        radius = new double[n];

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
            var center = layoutedComponent.getCenter();
            centerLocations[i][0] = center.x;
            centerLocations[i][1] = center.y;

            dispX[i] = 0;
            dispY[i] = 0;

            radius[i] = layoutedComponent.getRadius();
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
            List<Component> opposites = this.nodes.get(i).getOpposites();
            neighbours[i] = new int[opposites.size()];
            for (int j = 0; j < opposites.size(); j++) {
                Integer index = indices.get(getBoundsForComponents(opposites.get(j)));

                // Check the connected cell in part of the vertex list to be
                // acted on by this layout
                if (index != null) {
                    neighbours[i][j] = index;
                }
            }
        }

        temperature = initialTemp;
    }

    /**
     * Setup the layout and run the main loop until movement is cooled down (temperature is low).
     */
    public void execute() {

        setup();
        layoutLogger.recordLocations(centerLocations);

        // Main iteration loop
        for (iteration = 0; iteration < maxIterations; iteration++) {
            if (!allowedToRun) {
                return;
            }

            // Calculate repulsive forces on all vertices
            calcRepulsion();
            calcPositions();

            // Calculate attractive forces through edges
            calcAttraction();
            calcPositions();

            reduceTemperature();
        }

        for (int i = 0; i < nodes.size(); i++) {
            LayoutedComponent vertex = nodes.get(i);
            vertex.setX((long) centerLocations[i][0]);
            vertex.setY((long) centerLocations[i][1]);
        }
    }

    private LayoutedComponent getBoundsForComponents(Component component) {
        return nodes.stream().filter(bounds1 -> bounds1.getComponent().equals(component)).findFirst().orElse(null);
    }

    /**
     * Takes the displacements calculated for each cell and applies them to the
     * local cache of cell positions. Limits the displacement to the current
     * temperature.
     */
    protected void calcPositions() {
        for (int index = 0; index < nodes.size(); index++) {
            // Get the distance of displacement for this node for this
            // iteration

            Point2D.Double displacement = forces.applyDisplacement(centerLocations, radius, index, dispX[index], dispY[index], temperature);
            var newXDisp = displacement.x;
            var newYDisp = displacement.y;

            // Update the cached cell locations
            centerLocations[index][0] += newXDisp;
            centerLocations[index][1] += newYDisp;

            // reset displacements
            dispX[index] = 0;
            dispY[index] = 0;
            if (debug) {
                layoutLogger.debug("Iteration {} temp {}: Shifting index {} center by dx {} and dy {}", iteration, temperature, index, newXDisp, newYDisp);
            }

            //calculate new distances immediately
            for (int j = 0; j < nodes.size(); j++) {
                if (j == index) {
                    continue;
                }
                distances[index][j] = Geometry.getDistance(centerLocations[index], centerLocations[j], 0, 0, radius[index], radius[j]);
            }
        }
        layoutLogger.recordLocations(centerLocations);
    }

    /**
     * Calculates the attractive forces between all laid out nodes linked by
     * edges
     */
    protected void calcAttraction() {
        // Check the neighbours of each vertex and calculate the attractive
        // force of the edge connecting them
        for (int i = 0; i < nodes.size(); i++) {
            for (int k = 0; k < neighbours[i].length; k++) {
                // Get the index of the othe cell in the vertex array
                int j = neighbours[i][k];

                // Do not proceed self-loops
                if (i == j) {
                    continue;
                }

                Point2D.Double displacement = forces.getAttraction(centerLocations[i], centerLocations[j], radius[i], radius[j]);

                this.dispX[i] -= displacement.x;
                this.dispY[i] -= displacement.y;

                this.dispX[j] += displacement.x;
                this.dispY[j] += displacement.y;
            }
        }
    }

    /**
     * Calculates the repulsive forces between all laid out nodes
     */
    protected void calcRepulsion() {
        int vertexCount = nodes.size();

        for (int i = 0; i < vertexCount; i++) {
            for (int j = i; j < vertexCount; j++) {
                // Exits if the layout is no longer allowed to run
                if (!allowedToRun || j == i) {
                    continue;
                }

                Point2D.Double displacement = forces.getRepulsion(centerLocations[i], centerLocations[j], radius[i], radius[j]);

                dispX[i] += displacement.x;
                dispY[i] += displacement.y;

                dispX[j] -= displacement.x;
                dispY[j] -= displacement.y;
            }
        }
    }

    public List<LayoutedComponent> getNodes() {
        return nodes;
    }



    /**
     * Ensures that the given min distance is kept between all nodes.
     *
     * @throws IllegalStateException on shortfall
     */
    public void assertMinDistanceIsKept(double minDistanceLimit) {
        int vertexCount = nodes.size();

        for (int i = 0; i < vertexCount; i++) {
            for (int j = i; j < vertexCount; j++) {
                if (j == i) {
                    continue;
                }
                double deltaLengthWithRadius = Geometry.getDistance(centerLocations[i], centerLocations[j], 0, 0, radius[i], radius[j]);
                if (Math.abs(deltaLengthWithRadius) < minDistanceLimit * 0.9) {
                    throw new LayoutException(String.format("Min distance shortfall of %s/%s for i=%d j=%d", deltaLengthWithRadius, minDistanceLimit, i, j));
                }
            }
        }
    }

    public LayoutLogger getLayoutLogger() {
        return layoutLogger;
    }

}
