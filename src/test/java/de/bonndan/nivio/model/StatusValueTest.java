package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.dto.StatusDescription;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatusValueTest {

    @Test
    void highestOf() {
        List<StatusValue> statusValueSet = new ArrayList<>();
        statusValueSet.add(new StatusDescription("foo", Status.GREEN));
        statusValueSet.add(new StatusDescription("bar", Status.ORANGE));
        statusValueSet.add(new StatusDescription("baz", Status.ORANGE));

        List<StatusValue> highest = StatusValue.highestOf(statusValueSet);
        assertNotNull(highest);
        assertFalse(highest.isEmpty());
        assertEquals(2, highest.size());

        StatusValue first = highest.get(0);
        assertEquals(Status.ORANGE, first.getStatus());
        assertEquals("bar", first.getLabel());
    }
}