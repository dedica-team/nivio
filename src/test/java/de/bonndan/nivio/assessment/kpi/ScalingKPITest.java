package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ScalingKPITest {

    private ScalingKPI scalingKPI;
    private Item item;
    private Landscape landscape;
    private Item bar;
    private Group group;
    private GraphTestSupport graph;

    @BeforeEach
    public void setup() {
        scalingKPI = new ScalingKPI();
        scalingKPI.init(null);

        graph = new GraphTestSupport();
        landscape = graph.landscape;
        group = graph.getTestGroup("test");
        item = graph.getTestItem("test", "a");

        var foo = graph.getTestGroup("foo");
        bar = graph.getTestItem("foo", "bar");
    }

    @Test
    void unknownIfNoLabel() {
        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(0, statusValues.size());
    }

    @Test
    void unknownIfNotNumber() {

        item.setLabel(Label.scale, "foo");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(0, statusValues.size());
    }

    @Test
    void yellowIfZeroWithoutRelations() {
        item.setLabel(Label.scale, "0");

        //when
        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);

        //then
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.YELLOW, value.getStatus());
        assertEquals(Label.scale.name(), value.getField());
        assertEquals("scaled to zero", value.getMessage());
    }

    @Test
    void redIfZeroAsProvider() {
        item.setLabel(Label.scale, "0");
        Relation r1 = new Relation(item, bar, null, null, RelationType.PROVIDER);
        landscape.getIndexWriteAccess().addOrReplaceRelation(r1);

        //when
        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);

        //then
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.RED, value.getStatus());
        assertEquals("scaled to zero and provider for 1 items", value.getMessage());
    }

    @Test
    void orangeIfZeroAsDataTarget() {

        item.setLabel(Label.scale, "0");
        Relation r1 = new Relation(item, bar, null, null, RelationType.PROVIDER);
        landscape.getIndexWriteAccess().addOrReplaceRelation(r1);

        //when
        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);

        //then
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.ORANGE, value.getStatus());
        assertEquals("scaled to zero and data sink for 1 items", value.getMessage());
    }

    @Test
    void yellowIfBottleneck() {
        Relation r1 = new Relation(item, bar, null, null, RelationType.PROVIDER);

        Item testItem = graph.getTestItem("foo", "baz");
        landscape.getIndexWriteAccess().addOrReplaceChild(testItem);
        Relation r2 = new Relation(item, testItem, null, null, RelationType.PROVIDER);

        item.setLabel(Label.scale, "1");
        landscape.getIndexWriteAccess().addOrReplaceRelation(r1);
        landscape.getIndexWriteAccess().addOrReplaceRelation(r2);

        //when
        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);

        //then
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.YELLOW, value.getStatus());
        assertEquals("unscaled, but 2 items depend on it", value.getMessage());
    }

    @Test
    void greenIfNoRelations() {

        item.setLabel(Label.scale, "1");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.GREEN, value.getStatus());
    }

    @Test
    void greenWithRelations() {

        item.setLabel(Label.scale, "2");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.GREEN, value.getStatus());
    }
}