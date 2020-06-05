package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionKPITest {

    private ConditionKPI kpi;

    @BeforeEach
    public void setup() {
        kpi = new ConditionKPI();
    }

    @Test
    public void testGreen() {
        Item item = new Item();
        item.setIdentifier("foo");
        item.setLabel(Label.PREFIX_CONDITION + Label.DELIMITER + "bar", "True");

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertEquals(1, statusValues.size());
        assertEquals(Status.GREEN, statusValues.get(0).getStatus());
    }

    @Test
    public void testRed() {
        Item item = new Item();
        item.setIdentifier("foo");
        item.setLabel(Label.PREFIX_CONDITION + Label.DELIMITER + "bar", "True");
        item.setLabel(Label.PREFIX_CONDITION + Label.DELIMITER + "baz", "False");

        List<StatusValue> statusValues = kpi.getStatusValues(item);
        assertEquals(1, statusValues.size());
        assertEquals(Status.RED, statusValues.get(0).getStatus());
    }
}