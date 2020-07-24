package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * based on {@link com.mxgraph.layout.mxFastOrganicLayout}
 * Copyright (c) 2007, Gaudenz Alder
 * <p>
 * relevant changes marked with XXX
 */
public class NivioGroupLayout extends NivioMoreStaticLayout {

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
    protected double maxDistanceLimit = 300; //XXX was 500

    /**
     * Constructs a new fast organic layout for the specified graph.
     */
    public NivioGroupLayout(mxGraph graph) {
        super(graph);
    }

}
