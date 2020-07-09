package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScalingKPITest {

    @Test
    public void testScale1() {
        ScalingKPI scalingKPI = new ScalingKPI();
        scalingKPI.init(null);
        Item item = new Item();
        item.setLabel(Label.scale, "1");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertNotNull(value);
        assertEquals(Status.YELLOW, value.getStatus());
        assertEquals(Label.scale.toString().toLowerCase(), value.getField());
    }

    @Test
    public void testScale2() {
        ScalingKPI scalingKPI = new ScalingKPI();
        scalingKPI.init(null);
        Item item = new Item();
        item.setLabel(Label.scale, "2");

        List<StatusValue> statusValues = scalingKPI.getStatusValues(item);
        assertNotNull(statusValues);
        assertEquals(1, statusValues.size());
        StatusValue value = statusValues.get(0);
        assertEquals(Status.GREEN, value.getStatus());
    }
}