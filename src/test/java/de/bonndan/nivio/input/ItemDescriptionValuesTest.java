package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.InterfaceDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.model.RelationFactory;
import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ItemDescriptionValuesTest {

    @Test
    void incrementAddsIcon() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setIcon("foo");
        sd1.assignNotNull(increment);

        assertEquals("foo", sd1.getIcon());
    }

    @Test
    void incrementAddsIconSafely() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setIcon("foo");
        sd1.assignFromTemplate(increment);

        assertEquals("foo", sd1.getIcon());
    }

    @Test
    void incrementAddsAddress() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setAddress("foo");
        sd1.assignNotNull(increment);

        assertEquals("foo", sd1.getAddress());
    }

    @Test
    void incrementAddsLayer() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setLayer("foo");
        sd1.assignNotNull(increment);

        assertEquals("foo", sd1.getLayer());
    }

    @Test
    void incrementAddsAddressSafely() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        sd1.setAddress("sd1A");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setAddress("foo");


        sd1.assignFromTemplate(increment);

        assertEquals("sd1A", sd1.getAddress());
    }

    @Test
    void incrementAddsDataflow() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        RelationDescription other = RelationFactory.createDataflowDescription(sd1, "other");
        sd1.addOrReplaceRelation(other);

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        RelationDescription another = RelationFactory.createDataflowDescription(increment, "another");
        increment.addOrReplaceRelation(another);

        sd1.assignNotNull(increment);

        assertEquals(2, sd1.getRelations().size());
    }

    @Test
    void incrementUpdatesRelationDescription() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        RelationDescription other = RelationFactory.createDataflowDescription(sd1, "other");
        sd1.addOrReplaceRelation(other);

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        RelationDescription another = RelationFactory.createDataflowDescription(increment, "other");
        another.setLabel("foo", "bar");
        increment.addOrReplaceRelation(another);

        //when
        sd1.assignNotNull(increment);


        assertEquals(1, sd1.getRelations().size());
        RelationDescription relationDescription = sd1.getRelations().iterator().next();
        assertEquals("bar", relationDescription.getLabel("foo"));
    }

    @Test
    void incrementAddsNetworks() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        sd1.setPrefixed(Label.network, "net1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setPrefixed(Label.network, "net2");

        sd1.assignNotNull(increment);

        assertEquals(2, sd1.getLabels(Label.network).size());
    }

    @Test
    void incrementAddsProvidedBy() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        RelationDescription dbProvider = RelationFactory.createProviderDescription(sd1, "db1");
        sd1.addOrReplaceRelation(dbProvider);


        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        RelationDescription redisProvider = RelationFactory.createProviderDescription(sd1, "redis");
        increment.addOrReplaceRelation(redisProvider);

        sd1.assignNotNull(increment);

        assertEquals(2,
                sd1.getRelations().stream()
                        .filter(relation -> RelationType.PROVIDER.equals(relation.getType()))
                        .collect(Collectors.toUnmodifiableList()).size());
    }

    @Test
    void incrementAddsInterfaces() {

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


        sd1.assignNotNull(increment);

        assertEquals(2, sd1.getInterfaces().size());
    }

    @Test
    void incrementAddsLifecycle() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.setLifecycle(Lifecycle.END_OF_LIFE.name());


        sd1.assignNotNull(increment);

        assertEquals(Lifecycle.END_OF_LIFE.name(), sd1.getLabel(Label.lifecycle));
    }

    @Test
    void incrementAddsUnsetLabels() {

        ItemDescription sd1 = new ItemDescription();
        sd1.setIdentifier("sd1");
        sd1.getLabels().put("a", "1");

        ItemDescription increment = new ItemDescription();
        increment.setIdentifier("sd1");
        increment.getLabels().put("a", "2");
        increment.getLabels().put("b", "3");


        sd1.assignNotNull(increment);

        //a is overwritten
        assertEquals("2", sd1.getLabels().get("a"));
        //b is new
        assertEquals("3", sd1.getLabels().get("b"));
    }
}
