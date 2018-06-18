package de.bonndan.nivio.output;

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
        a.setType(LandscapeItem.APPLICATION);
        a.setIdentifier("a");
        landscape.addService(a);

        Service a1 = new Service();
        a1.setType(LandscapeItem.INFRASTRUCTURE);
        a1.setIdentifier("a1");
        a.getProvidedBy().add(a1);

        Service b = new Service();
        b.setType(LandscapeItem.APPLICATION);
        b.setIdentifier("b");
        landscape.addService(b);
    }

    @Test
    public void build(){
        GraphBuilder graphBuilder = new GraphBuilder();
        Graph graph = graphBuilder.build(landscape);
        Assertions.assertNotNull(graph);

        Object a1 = graph.getEdge(landscape.getService("a"), landscape.getService("a1"));
        Assertions.assertNotNull(a1);

        Object a2b = graph.getEdge(landscape.getService("a"), landscape.getService("b"));
        Assertions.assertNotNull(a2b);
    }
}
