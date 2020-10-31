package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.model.RelationBuilder;
import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ItemDescriptionValuesTest {

    @Test
    public void incrementAddsDataflow() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        RelationDescription other = RelationBuilder.createDataflowDescription(sd1, "other");
        sd1.addRelation(other);

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        RelationDescription another = RelationBuilder.createDataflowDescription(increment, "another");
        increment.addRelation(another);

        ItemDescriptionValues.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getRelations().size());
    }

    @Test
    public void incrementAddsNetworks() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        sd1.setPrefixed(Label.network, "net1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setPrefixed(Label.network, "net2");

        ItemDescriptionValues.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getLabels(Label.network).size());
    }

    @Test
    public void incrementAddsProvidedBy() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        RelationDescription dbProvider = RelationBuilder.createProviderDescription(sd1, "db1");
        sd1.addRelation(dbProvider);


        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        RelationDescription redisProvider = RelationBuilder.createProviderDescription(sd1, "redis");
        increment.addRelation(redisProvider);

        ItemDescriptionValues.assignNotNull(sd1, increment);

        assertEquals(2,
                sd1.getRelations().stream()
                .filter(relation -> RelationType.PROVIDER.equals(relation.getType()))
                .collect(Collectors.toUnmodifiableList()).size());
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


        ItemDescriptionValues.assignNotNull(sd1, increment);

        assertEquals(2, sd1.getInterfaces().size());
    }

    @Test
    public void incrementAddsLifecycle() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setLifecycle(Lifecycle.END_OF_LIFE.name());


        ItemDescriptionValues.assignNotNull(sd1, increment);

        assertEquals(Lifecycle.END_OF_LIFE.name(), sd1.getLabel(Label.lifecycle));
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


        ItemDescriptionValues.assignNotNull(sd1, increment);

        //a is overwritten
        assertEquals("2", sd1.getLabels().get("a"));
        //b is new
        assertEquals("3", sd1.getLabels().get("b"));
    }
}