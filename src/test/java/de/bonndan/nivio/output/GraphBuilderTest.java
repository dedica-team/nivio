package de.bonndan.nivio.output;

import de.bonndan.nivio.landscape.DataFlow;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeItem;
import de.bonndan.nivio.landscape.Service;
import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class GraphBuilderTest {

    private Landscape landscape= new Landscape();

    @Before
    public void setUp() {
        Service a = new Service();
        a.setType(LandscapeItem.TYPE_APPLICATION);
        a.setIdentifier("a");
        landscape.addService(a);

        Service a1 = new Service();
        a1.setType(LandscapeItem.TYPE_INFRASTRUCTURE);
        a1.setIdentifier("a1");
        a.getProvidedBy().add(a1);



        Service b = new Service();
        b.setType(LandscapeItem.TYPE_APPLICATION);
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

        Assertions.assertTrue(graph.containsVertex(landscape.getService("a")));

        Object a1 = graph.getEdge(landscape.getService("a"), landscape.getService("a1"));
        Assertions.assertNotNull(a1);

        Object a2b = graph.getEdge(landscape.getService("a"), landscape.getService("b"));
        Assertions.assertNotNull(a2b);
    }
}
