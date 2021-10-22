package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.junit.jupiter.api.Assertions.*;

class ScalingKPITest {

    private ScalingKPI scalingKPI;
    private Item item;

    @BeforeEach
    public void setup() {
        scalingKPI = new ScalingKPI();
        scalingKPI.init(null);
        item = getTestItem("test", "a");
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
        Relation r1 = new Relation(item, getTestItem("foo", "bar"), null, null, RelationType.PROVIDER);

        item = getTestItemBuilder("test", "a").withRelations(Set.of(r1)).build();
        item.setLabel(Label.scale, "0");

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
        Relation r1 = new Relation(getTestItem("foo", "bar"), item, null, null, RelationType.DATAFLOW);

        item = getTestItemBuilder("test", "a").withRelations(Set.of(r1)).build();
        item.setLabel(Label.scale, "0");

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
        Relation r1 = new Relation(item, getTestItem("foo", "bar"), null, null, RelationType.PROVIDER);
        Relation r2 = new Relation(item, getTestItem("foo", "baz"), null, null, RelationType.PROVIDER);

        item = getTestItemBuilder("test", "a").withRelations(Set.of(r1,r2)).build();
        item.setLabel(Label.scale, "1");

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
    public void greenIfNoRelations() {

        item.setLabel(Label.scale, "1");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.GREEN, value.getStatus());
    }

    @Test
    public void greenWithRelations() {

        item.setLabel(Label.scale, "2");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.GREEN, value.getStatus());
    }
}