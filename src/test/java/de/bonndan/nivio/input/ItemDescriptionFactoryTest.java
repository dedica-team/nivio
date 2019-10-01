package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.DataFlowDescription;
import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.StatusDescription;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.model.Status;
import de.bonndan.nivio.model.StatusItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemDescriptionFactoryTest {

    @Test
    public void incrementAddsDataflow() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        DataFlowDescription df1 = new DataFlowDescription();
        df1.setSource("sd1");
        df1.setTarget("other");
        sd1.addDataFlow(df1);

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        DataFlowDescription df2 = new DataFlowDescription();
        df2.setSource("sd1");
        df2.setTarget("another");
        increment.addDataFlow(df2);

        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getDataFlow().size());
    }

    @Test
    public void incrementAddsNetworks() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        sd1.getNetworks().add("net1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.getNetworks().add("net2");

        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getNetworks().size());
    }

    @Test
    public void incrementAddsStatuses() {
        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        sd1.setStatus(new StatusDescription(StatusItem.CAPABILITY, Status.GREEN));

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setStatus(new StatusDescription(StatusItem.STABILITY, Status.GREEN));

        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getStatuses().size());
    }

    @Test
    public void incrementAddsProvidedBy() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        sd1.getProvided_by().add("db1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.getProvided_by().add("redis");

        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getProvided_by().size());
    }

    @Test
    public void incrementAddsInterfaces() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");

        InterfaceDescription if1 = new InterfaceDescription();
        if1.setDescription("api");
        sd1.getInterfaces().add(if1);

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        InterfaceDescription if2 = new InterfaceDescription();
        if2.setDescription("ftp");
        increment.getInterfaces().add(if2);


        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getInterfaces().size());
    }

    @Test
    public void incrementAddsLifecycle() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setLifecycle(Lifecycle.END_OF_LIFE);


        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(Lifecycle.END_OF_LIFE, sd1.getLifecycle());
    }

    @Test
    public void incrementAddsUnsetLabels() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        sd1.getLabels().put("a", "1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.getLabels().put("a", "2");
        increment.getLabels().put("b", "3");


        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals("1", sd1.getLabels().get("a"));
        assertEquals("3", sd1.getLabels().get("b"));
    }
}
