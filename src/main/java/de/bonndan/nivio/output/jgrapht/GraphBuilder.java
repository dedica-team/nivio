package de.bonndan.nivio.output.jgrapht;

import de.bonndan.nivio.landscape.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.stereotype.Component;

@Component
public class GraphBuilder {

    private Graph<ServiceItem, LabeledEdge> graph;
    private Landscape landscape;

    public Graph build(Landscape landscape) {
        graph = new SimpleGraph<>(LabeledEdge.class);
        this.landscape = landscape;
        landscape.getServices().forEach(this::addService);
        landscape.getServices().forEach(this::addLinks);
        return graph;
    }

    private void addService(Service service) {
        graph.addVertex(service);
        service.getProvidedBy().forEach(infra -> {
            addService(infra);
            graph.addEdge(infra, service, new LabeledEdge("provides"));
        });
    }

    private void addLinks(Service service) {
        service.getDataFlow().forEach(flow -> {
            ServiceItem target = ServiceItems.pick(flow.getTarget(), null, landscape.getServices());
            graph.addEdge(service, target, new LabeledEdge(flow.getDescription()));
        });
    }
}
