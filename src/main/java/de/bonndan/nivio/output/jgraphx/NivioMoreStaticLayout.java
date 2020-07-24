package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.List;


/**
 * Fast organic layout without random influence.
 *
 * based on {@link com.mxgraph.layout.mxFastOrganicLayout}
 * Copyright (c) 2007, Gaudenz Alder
 * <p>
 * relevant changes marked with XXX
 */
public class NivioMoreStaticLayout extends mxFastOrganicLayout {

    /**
     * The force constant by which the attractive forces are divided and the
     * replusive forces are multiple by the square of. The value equates to the
     * average radius there is of free space around each node. Default is 50.
     */
    protected double forceConstant = 100;

    /**
     * The maximum distance between vertices, beyond which their
     * repulsion no longer has an effect
     */
    protected double maxDistanceLimit = 300;

    /**
     * Constructs a new fast organic layout for the specified graph.
     */
    public NivioMoreStaticLayout(mxGraph graph) {
        super(graph);
    }

    /**
     * Returns a boolean indicating if the given <mxCell> should be ignored as a
     * vertex. This returns true if the cell has no connections.
     *
     * @param vertex Object that represents the vertex to be tested.
     * @return Returns true if the vertex should be ignored.
     */
    public boolean isVertexIgnored(Object vertex) {
        // XXX || graph.getConnections(vertex).length == 0;
        // XXX we must take unconnected into account, so using the original method
        return !this.graph.getModel().isVertex(vertex) || !this.graph.isCellVisible(vertex);
    }

    /* (non-Javadoc)
     * @see com.mxgraph.layout.mxIGraphLayout#execute(java.lang.Object)
     */
    public void execute(Object parent) {
        mxIGraphModel model = graph.getModel();

        // Finds the relevant vertices for the layout
        Object[] vertices = graph.getChildVertices(parent);
        List<Object> tmp = new ArrayList<>(vertices.length);

        for (Object o : vertices) {
            if (!isVertexIgnored(o))
                tmp.add(o);
        }

        vertexArray = tmp.toArray();
        mxRectangle initialBounds = (useInputOrigin) ? graph.getBoundsForCells(
                vertexArray, false, false, true) : null;
        int n = vertexArray.length;

        dispX = new double[n];
        dispY = new double[n];
        cellLocation = new double[n][];
        isMoveable = new boolean[n];
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
        for (int i = 0; i < vertexArray.length; i++) {
            Object vertex = vertexArray[i];
            cellLocation[i] = new double[2];

            // Set up the mapping from array indices to cells
            indices.put(vertex, i);
            mxRectangle bounds = getVertexBounds(vertex);

            // Set the X,Y value of the internal version of the cell to
            // the center point of the vertex for better positioning
            double width = bounds.getWidth();
            double height = bounds.getHeight();

            // Randomize (0, 0) locations
            double x = bounds.getX();
            double y = bounds.getY();

            cellLocation[i][0] = x + width / 2.0;
            cellLocation[i][1] = y + height / 2.0;

            radius[i] = Math.min(width, height); //XXX could be changed to Math.max
            radiusSquared[i] = radius[i] * radius[i];
        }

        // Moves cell location back to top-left from center locations used in
        // algorithm, resetting the edge points is part of the transaction
        model.beginUpdate();
        try {
            prepareData(parent, n);

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

            Double minx = null;
            Double miny = null;

            for (int i = 0; i < vertexArray.length; i++) {
                Object vertex = vertexArray[i];
                mxGeometry geo = model.getGeometry(vertex);

                if (geo != null) {
                    cellLocation[i][0] -= geo.getWidth() / 2.0;
                    cellLocation[i][1] -= geo.getHeight() / 2.0;

                    double x = graph.snap(cellLocation[i][0]);
                    double y = graph.snap(cellLocation[i][1]);
                    setVertexLocation(vertex, x, y);

                    if (minx == null) {
                        minx = x;
                    } else {
                        minx = Math.min(minx, x);
                    }

                    if (miny == null) {
                        miny = y;
                    } else {
                        miny = Math.min(miny, y);
                    }
                }
            }

            // Modifies the cloned geometries in-place. Not needed
            // to clone the geometries again as we're in the same
            // undoable change.
            double dx = (minx != null) ? -minx - 1 : 0;
            double dy = (miny != null) ? -miny - 1 : 0;

            if (initialBounds != null) {
                dx += initialBounds.getX();
                dy += initialBounds.getY();
            }

            graph.moveCells(vertexArray, dx, dy);
        } finally {
            model.endUpdate();
        }
    }

    // Get lists of neighbours to all vertices, translate the cells
    // obtained in indices into vertexArray and store as an array
    // against the original cell index
    private void prepareData(Object parent, int n) {
        for (int i = 0; i < n; i++) {
            dispX[i] = 0;
            dispY[i] = 0;
            isMoveable[i] = isVertexMovable(vertexArray[i]);


            Object[] edges = graph.getConnections(vertexArray[i], parent);
            for (int k = 0; k < edges.length; k++) {
                if (isResetEdges()) {
                    graph.resetEdge(edges[k]);
                }

                if (isDisableEdgeStyle()) {
                    setEdgeStyleEnabled(edges[k], false);
                }
            }

            Object[] cells = graph.getOpposites(edges, vertexArray[i]);

            neighbours[i] = new int[cells.length];

            for (int j = 0; j < cells.length; j++) {
                Integer index = indices.get(cells[j]);

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
                }
            }
        }
    }

    /**
     * Calculates the repulsive forces between all laid out nodes
     */
    protected void calcRepulsion() {
        int vertexCount = vertexArray.length;

        for (int i = 0; i < vertexCount; i++) {
            for (int j = i; j < vertexCount; j++) {
                // Exits if the layout is no longer allowed to run
                if (!allowedToRun) {
                    return;
                }

                if (j == i) {
                    continue;
                }

                double xDelta = cellLocation[i][0] - cellLocation[j][0];
                double yDelta = cellLocation[i][1] - cellLocation[j][1];

                if (xDelta == 0) {
                    //XXX was xDelta = 0.01 + Math.random();
                    xDelta = 0.01;
                }

                if (yDelta == 0) {
                    //XXX was yDelta = 0.01 + Math.random();
                    yDelta = 0.01;
                }

                // Distance between nodes
                double deltaLength = Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
                double deltaLengthWithRadius = deltaLength - radius[i] - radius[j];

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

                if (isMoveable[i]) {
                    dispX[i] += displacementX;
                    dispY[i] += displacementY;
                }

                if (isMoveable[j]) {
                    dispX[j] -= displacementX;
                    dispY[j] -= displacementY;
                }
            }
        }
    }

}
