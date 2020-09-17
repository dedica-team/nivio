package de.bonndan.nivio.assessment;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatusValueTest {

    @Test
    void mustHaveField() {
        assertThrows(IllegalArgumentException.class, () -> {new StatusValue(null, Status.GREEN);});
    }

    @Test
    void highestOf() {
        List<StatusValue> statusValueSet = new ArrayList<>();
        statusValueSet.add(new StatusValue("foo", Status.GREEN));
        statusValueSet.add(new StatusValue("bar", Status.ORANGE));
        statusValueSet.add(new StatusValue("baz", Status.ORANGE));

        List<StatusValue> highest = StatusValue.highestOf(statusValueSet);
        assertNotNull(highest);
        assertFalse(highest.isEmpty());
        assertEquals(2, highest.size());

        StatusValue first = highest.get(0);
        assertEquals(Status.ORANGE, first.getStatus());
        assertEquals("bar", first.getField());
    }

    @Test
    void comparator() {
        StatusValue green = new StatusValue("foo", Status.GREEN);
        StatusValue red = new StatusValue("foo", Status.RED);
        assertEquals(-1, new StatusValue.Comparator().compare(green, red));
        assertEquals(0, new StatusValue.Comparator().compare(green, green));
        assertEquals(1, new StatusValue.Comparator().compare(red, green));
    }

    @Test
    void isNotSummary() {
        StatusValue statusValue = new StatusValue("security", Status.BROWN, "epically broken");
        assertFalse(statusValue.isSummary());
    }


    @Test
    void summary() {
        StatusValue summary = StatusValue.summary("foo.bar", new StatusValue("security", Status.BROWN, "epically broken"));
        assertNotNull(summary);
        assertEquals("foo.bar", summary.getField());
        assertEquals("epically broken", summary.getMessage());
        assertEquals(Status.BROWN, summary.getStatus());
        assertEquals("security", summary.getMaxField());
        assertTrue(summary.isSummary());
    }
}