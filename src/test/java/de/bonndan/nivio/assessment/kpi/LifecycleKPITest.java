package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LifecycleKPITest {

    LifecycleKPI kpi = new LifecycleKPI();

    @BeforeEach
    public void setup() {
        kpi.init(null);
    }

    @Test
    public void green() {
        Item item = new Item();
        item.setIdentifier("foo");
        item.setLabel(Label.lifecycle, Lifecycle.PRODUCTION.name());

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue statusValue = statusValues.get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
        assertEquals("lifecycle: PRODUCTION", statusValue.getMessage());
    }

    @Test
    public void orange() {
        Item item = new Item();
        item.setIdentifier("foo");
        item.setLabel(Label.lifecycle, Lifecycle.END_OF_LIFE.name());

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue statusValue = statusValues.get(0);
        assertEquals(Status.ORANGE, statusValue.getStatus());
    }

    @Test
    public void none1() {
        Item item = new Item();
        item.setIdentifier("foo");
        item.setLabel(Label.lifecycle, "foo");

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(0, statusValues.size());
    }

    @Test
    public void none2() {
        Item item = new Item();
        item.setIdentifier("foo");

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(0, statusValues.size());
    }
}