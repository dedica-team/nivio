package de.bonndan.nivio.output;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.jgrapht.GraphBuilder;
import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class GraphBuilderTest {

    private LandscapeImpl landscape= new LandscapeImpl();

    @Before
    public void setUp() {
        Item a = new Item();
        a.setLayer(LandscapeItem.LAYER_APPLICATION);
        a.setIdentifier("a");
        landscape.addItem(a);

        Item a1 = new Item();
        a1.setLayer(LandscapeItem.LAYER_INFRASTRUCTURE);
        a1.setIdentifier("a1");
        a.getRelations().add(RelationBuilder.createProviderRelation(a1, a));
        landscape.addItem(a1);

        Item b = new Item();
        b.setLayer(LandscapeItem.LAYER_INGRESS);
        b.setIdentifier("b");
        landscape.addItem(b);

        Relation df = new Relation(a, b);
        df.setFormat("json");
        df.setDescription("push");
        a.getRelations().add(df);
    }

    @Test
    public void build(){
        GraphBuilder graphBuilder = new GraphBuilder();
        Graph graph = graphBuilder.build(landscape);
        Assertions.assertNotNull(graph);

        Item a = (Item) ServiceItems.pick("a", null, landscape.getItems());
        Item a1 = (Item) ServiceItems.pick("a1", null, landscape.getItems());
        Item b = (Item) ServiceItems.pick("b", null, landscape.getItems());
        Assertions.assertTrue(graph.containsVertex(a));

        Object edge1 = graph.getEdge(a, a1);
        Assertions.assertNotNull(edge1);

        Object edge2 = graph.getEdge(a, b);
        Assertions.assertNotNull(edge2);
    }
}
