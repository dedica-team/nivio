package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeItem;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphBuilder {

    private final Landscape landscape;

    public GraphBuilder(Landscape landscape) {
        this.landscape = landscape;
    }

    public Graph build() {
        Graph<LandscapeItem, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        return graph;
    }
}
