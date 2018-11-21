package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.*;
import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class GraphBuilderTest {

    private Landscape landscape= new Landscape();

    @Before
    public void setUp() {
        Service a = new Service();
        a.setLayer(LandscapeItem.LAYER_APPLICATION);
        a.setIdentifier("a");
        landscape.addService(a);

        Service a1 = new Service();
        a1.setLayer(LandscapeItem.LAYER_INFRASTRUCTURE);
        a1.setIdentifier("a1");
        a.getProvidedBy().add(a1);
        landscape.addService(a1);

        Service b = new Service();
        b.setLayer(LandscapeItem.LAYER_INGRESS);
        b.setIdentifier("b");
        landscape.addService(b);

        DataFlow df = new DataFlow(a, b);
        df.setFormat("json");
        df.setDescription("push");
        a.getDataFlow().add(df);
    }

    @Test
    public void build(){
        GraphBuilder graphBuilder = new GraphBuilder();
        Graph graph = graphBuilder.build(landscape);
        Assertions.assertNotNull(graph);

        Service a = Utils.pick("a", landscape.getServices());
        Service a1 = Utils.pick("a1", landscape.getServices());
        Service b = Utils.pick("b", landscape.getServices());
        Assertions.assertTrue(graph.containsVertex(a));

        Object edge1 = graph.getEdge(a, a1);
        Assertions.assertNotNull(edge1);

        Object edge2 = graph.getEdge(a, b);
        Assertions.assertNotNull(edge2);
    }
}
