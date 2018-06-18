package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeItem;
import de.bonndan.nivio.landscape.Service;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.stereotype.Component;

@Component
public class GraphBuilder {

    private Graph<LandscapeItem, DefaultEdge> graph;

    public Graph build(Landscape landscape) {
        graph = new SimpleGraph<>(DefaultEdge.class);

        landscape.getServices().forEach(this::addService);
        return graph;
    }

    private void addService(Service service) {
        graph.addVertex(service);
        service.getProvidedBy().forEach(infra -> {
            addService(infra);
            graph.addEdge(infra, service);
        });
    }
}
