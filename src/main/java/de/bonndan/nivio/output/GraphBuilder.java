package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.DataFlow;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.landscape.Service;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.stereotype.Component;

@Component
public class GraphBuilder {

    private Graph<ServiceItem, LabeledEdge> graph;

    public Graph build(Landscape landscape) {
        graph = new SimpleGraph<>(LabeledEdge.class);

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
            graph.addEdge(service, ((DataFlow)flow).getTargetEntity(), new LabeledEdge(flow.getDescription()));
        });
    }
}
