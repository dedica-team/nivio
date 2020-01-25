package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.StatusDescription;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StatusItemTest {

    @Test
    void highestOf() {
        Set<StatusItem> statusItemSet = new HashSet<>();
        statusItemSet.add(new StatusDescription("foo", Status.GREEN));
        statusItemSet.add(new StatusDescription("bar", Status.ORANGE));
        statusItemSet.add(new StatusDescription("baz", Status.ORANGE));

        List<StatusItem> highest = StatusItem.highestOf(statusItemSet);
        assertNotNull(highest);
        assertFalse(highest.isEmpty());
        assertEquals(2, highest.size());

        StatusItem first = highest.get(0);
        assertEquals(Status.ORANGE, first.getStatus());
        assertEquals("bar", first.getLabel());
    }
}