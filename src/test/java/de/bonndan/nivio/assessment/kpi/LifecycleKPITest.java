package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LifecycleKPITest {

    LifecycleKPI kpi = new LifecycleKPI();

    @BeforeEach
    public void setup() {
        kpi.init(null);
    }

    @Test
    public void green() {
        Item item = getTestItem("null", "foo");
        item.setLabel(Label.lifecycle, Lifecycle.PRODUCTION.name());

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue statusValue = statusValues.get(0);
        assertEquals(Status.GREEN, statusValue.getStatus());
        assertEquals("Phase: production", statusValue.getMessage());
    }

    @Test
    public void orange() {
        Item item = getTestItem("null", "foo");
        item.setLabel(Label.lifecycle, Lifecycle.END_OF_LIFE.name());

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue statusValue = statusValues.get(0);
        assertEquals(Status.ORANGE, statusValue.getStatus());
    }

    @Test
    public void none1() {
        Item item = getTestItem("null", "foo");
        item.setLabel(Label.lifecycle, "foo");

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(0, statusValues.size());
    }

    @Test
    public void none2() {
        Item item = getTestItem("null", "foo");

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(0, statusValues.size());
    }
}
