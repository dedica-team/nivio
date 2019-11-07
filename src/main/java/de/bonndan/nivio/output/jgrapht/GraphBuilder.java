package de.bonndan.nivio.output.jgrapht;

import de.bonndan.nivio.model.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.stereotype.Component;

@Component
public class GraphBuilder {

    private Graph<LandscapeItem, LabeledEdge> graph;
    private LandscapeImpl landscape;

    public Graph build(LandscapeImpl landscape) {
        graph = new SimpleGraph<>(LabeledEdge.class);
        this.landscape = landscape;
        landscape.getItems().forEach(this::addService);
        landscape.getItems().forEach(this::addLinks);
        return graph;
    }

    private void addService(Item item) {
        graph.addVertex(item);
        item.getProvidedBy().forEach(infra -> {
            addService(infra);
            graph.addEdge(infra, item, new LabeledEdge("provides"));
        });
    }

    private void addLinks(Item item) {
        item.getRelations().stream()
                .filter(rel -> rel.getSource().equals(item))
                .forEach(flow -> {
                    LandscapeItem target = ServiceItems.pick(flow.getTarget().getIdentifier(), null, landscape.getItems());
                    graph.addEdge(item, target, new LabeledEdge(flow.getDescription()));
                });
    }
}
