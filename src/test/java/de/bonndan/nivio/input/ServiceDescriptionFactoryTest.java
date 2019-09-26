package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.DataFlowDescription;
import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.StatusDescription;
import de.bonndan.nivio.landscape.Lifecycle;
import de.bonndan.nivio.landscape.Status;
import de.bonndan.nivio.landscape.StatusItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceDescriptionFactoryTest {

    @Test
    public void incrementAddsDataflow() {

        ServiceDescription sd1 = new ServiceDescription();
        sd1.setIdentifier("sd1");
        DataFlowDescription df1 = new DataFlowDescription();
        df1.setSource("sd1");
        df1.setTarget("other");
        sd1.addDataFlow(df1);

        ServiceDescription increment = new ServiceDescription();
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

        ServiceDescription sd1 = new ServiceDescription();
        sd1.setIdentifier("sd1");
        sd1.getNetworks().add("net1");

        ServiceDescription increment = new ServiceDescription();
        increment.setIdentifier("sd1");
        increment.getNetworks().add("net2");

        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getNetworks().size());
    }

    @Test
    public void incrementAddsStatuses() {
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setIdentifier("sd1");
        sd1.setStatus(new StatusDescription(StatusItem.CAPABILITY, Status.GREEN));

        ServiceDescription increment = new ServiceDescription();
        increment.setIdentifier("sd1");
        increment.setStatus(new StatusDescription(StatusItem.STABILITY, Status.GREEN));

        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getStatuses().size());
    }

    @Test
    public void incrementAddsProvidedBy() {

        ServiceDescription sd1 = new ServiceDescription();
        sd1.setIdentifier("sd1");
        sd1.getProvided_by().add("db1");

        ServiceDescription increment = new ServiceDescription();
        increment.setIdentifier("sd1");
        increment.getProvided_by().add("redis");

        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getProvided_by().size());
    }

    @Test
    public void incrementAddsInterfaces() {

        ServiceDescription sd1 = new ServiceDescription();
        sd1.setIdentifier("sd1");

        InterfaceDescription if1 = new InterfaceDescription();
        if1.setDescription("api");
        sd1.getInterfaces().add(if1);

        ServiceDescription increment = new ServiceDescription();
        increment.setIdentifier("sd1");
        InterfaceDescription if2 = new InterfaceDescription();
        if2.setDescription("ftp");
        increment.getInterfaces().add(if2);


        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getInterfaces().size());
    }

    @Test
    public void incrementAddsLifecycle() {

        ServiceDescription sd1 = new ServiceDescription();
        sd1.setIdentifier("sd1");

        ServiceDescription increment = new ServiceDescription();
        increment.setIdentifier("sd1");
        increment.setLifecycle(Lifecycle.END_OF_LIFE);


        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals(Lifecycle.END_OF_LIFE, sd1.getLifecycle());
    }

    @Test
    public void incrementAddsUnsetLabels() {

        ServiceDescription sd1 = new ServiceDescription();
        sd1.setIdentifier("sd1");
        sd1.getLabels().put("a", "1");

        ServiceDescription increment = new ServiceDescription();
        increment.setIdentifier("sd1");
        increment.getLabels().put("a", "2");
        increment.getLabels().put("b", "3");


        ServiceDescriptionFactory.assignNotNull(sd1, increment);

        assertEquals("1", sd1.getLabels().get("a"));
        assertEquals("3", sd1.getLabels().get("b"));
    }
}
