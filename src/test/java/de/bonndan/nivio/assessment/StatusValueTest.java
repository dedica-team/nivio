package de.bonndan.nivio.assessment;

import org.junit.jupiter.api.Test;

import java.util.*;

import static de.bonndan.nivio.assessment.StatusValue.LABEL_SUFFIX_MESSAGE;
import static de.bonndan.nivio.assessment.StatusValue.LABEL_SUFFIX_STATUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StatusValueTest {

    @Test
    void mustHaveField() {
        assertThrows(IllegalArgumentException.class, () -> {new StatusValue(null, Status.GREEN);});
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

    @Test
    void fromMapping() {

        //given
        Map<String, Map<String, String>> input = new HashMap<>();
        input.put("security", new HashMap<>());
        input.get("security").put(LABEL_SUFFIX_STATUS, Status.ORANGE.getName());
        input.get("security").put(LABEL_SUFFIX_MESSAGE, "foobar");

        //when
        Set<StatusValue> statusValues = StatusValue.fromMapping(input);
        assertFalse(statusValues.isEmpty());
        StatusValue security = statusValues.iterator().next();
        assertEquals("security", security.getField());
        assertEquals("foobar", security.getMessage());
        assertEquals(Status.ORANGE, security.getStatus());
    }

    @Test
    void isEqual() {
        //equality on field basis only to guarantee uniqueness
        assertThat(new StatusValue("foo", Status.GREEN)).isEqualTo(new StatusValue("foo", Status.RED));
    }
}