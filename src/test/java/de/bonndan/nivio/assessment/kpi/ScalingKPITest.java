package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ScalingKPITest {

    private ScalingKPI scalingKPI;
    private Item testA;
    private Landscape landscape;
    private Item fooBar;
    private GraphTestSupport graph;

    @BeforeEach
    public void setup() {
        scalingKPI = new ScalingKPI();
        scalingKPI.init(null);

        graph = new GraphTestSupport();
        landscape = graph.landscape;
        graph.getTestGroup("test");
        testA = graph.getTestItem("test", "a");

        graph.getTestGroup("foo");
        fooBar = graph.getTestItem("foo", "bar");
    }

    @Test
    void unknownIfNoLabel() {
        List<StatusValue> statusValues = scalingKPI.getStatusValues(testA);
        assertNotNull(statusValues);
        assertEquals(0, statusValues.size());
    }

    @Test
    void unknownIfNotNumber() {

        testA.setLabel(Label.scale, "foo");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(testA);
        assertNotNull(statusValues);
        assertEquals(0, statusValues.size());
    }

    @Test
    void yellowIfZeroWithoutRelations() {
        testA.setLabel(Label.scale, "0");

        //when
        List<StatusValue> statusValues = scalingKPI.getStatusValues(testA);

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
        testA.setLabel(Label.scale, "0");
        Relation r1 = new Relation(testA, fooBar, null, null, RelationType.PROVIDER);
        landscape.getWriteAccess().addOrReplaceRelation(r1);

        //when
        List<StatusValue> statusValues = scalingKPI.getStatusValues(testA);

        //then
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.RED, value.getStatus());
        assertEquals("scaled to zero and provider for 1 items", value.getMessage());
    }

    @Test
    void orangeIfZeroAsDataTarget() {

        testA.setLabel(Label.scale, "0");
        Relation r1 = new Relation(fooBar, testA, null, null, RelationType.DATAFLOW);
        landscape.getWriteAccess().addOrReplaceRelation(r1);

        //when
        List<StatusValue> statusValues = scalingKPI.getStatusValues(testA);

        //then
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.ORANGE, value.getStatus());
        assertEquals("scaled to zero and data sink for 1 items", value.getMessage());
    }

    @Test
    void yellowIfBottleneck() {
        Relation r1 = new Relation(testA, fooBar, null, null, RelationType.PROVIDER);

        Item testItem = graph.getTestItem("foo", "baz");
        Relation r2 = new Relation(testA, testItem, null, null, RelationType.PROVIDER);

        testA.setLabel(Label.scale, "1");
        landscape.getWriteAccess().addOrReplaceRelation(r1);
        landscape.getWriteAccess().addOrReplaceRelation(r2);

        //when
        List<StatusValue> statusValues = scalingKPI.getStatusValues(testA);

        //then
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.YELLOW, value.getStatus());
        assertEquals("unscaled, but 2 items depend on it", value.getMessage());
    }

    @Test
    void greenIfNoRelations() {

        testA.setLabel(Label.scale, "1");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(testA);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.GREEN, value.getStatus());
    }

    @Test
    void greenWithRelations() {

        testA.setLabel(Label.scale, "2");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(testA);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.GREEN, value.getStatus());
    }
}