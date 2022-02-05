package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.bonndan.nivio.assessment.StatusValue.LABEL_SUFFIX_MESSAGE;
import static de.bonndan.nivio.assessment.StatusValue.LABEL_SUFFIX_STATUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StatusValueTest {

    private Landscape foo;

    @BeforeEach
    void setup() {
        foo = LandscapeFactory.createForTesting("foo", "foo").build();
    }

    @Test
    void mustHaveField() {
        assertThrows(NullPointerException.class, () -> new StatusValue(null, "foo", Status.GREEN, ""));
        assertThrows(IllegalArgumentException.class, () -> new StatusValue(foo.getFullyQualifiedIdentifier(), null, Status.GREEN, ""));
    }

    @Test
    void comparator() {
        StatusValue green = new StatusValue(foo.getFullyQualifiedIdentifier(), "bar", Status.GREEN, "");
        StatusValue red = new StatusValue(foo.getFullyQualifiedIdentifier(), "bar", Status.RED, "");
        StatusValue yellow = new StatusValue(foo.getFullyQualifiedIdentifier(), "bar", Status.YELLOW, "");
        assertEquals(-3, new StatusValue.Comparator().compare(green, red));
        assertEquals(0, new StatusValue.Comparator().compare(green, green));
        assertEquals(1, new StatusValue.Comparator().compare(yellow, green));
        assertEquals(3, new StatusValue.Comparator().compare(red, green));
    }

    @Test
    void isNotSummary() {
        StatusValue statusValue = new StatusValue(foo.getFullyQualifiedIdentifier(), "security", Status.BROWN, "epically broken");
        assertFalse(statusValue.isSummary());
    }


    @Test
    void summary() {
        StatusValue summary = StatusValue.summary(foo.getFullyQualifiedIdentifier(), Collections.singletonList(new StatusValue(foo.getFullyQualifiedIdentifier(), "security", Status.BROWN, "epically broken")));
        assertNotNull(summary);
        assertEquals(foo.getFullyQualifiedIdentifier(), summary.getIdentifier());
        assertEquals(StatusValue.SUMMARY_FIELD_VALUE, summary.getField());
        assertThat(summary.getMessage()).contains("epically broken").contains("security");
        assertEquals(Status.BROWN, summary.getStatus());
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
        Set<StatusValue> statusValues = StatusValue.fromMapping(foo.getFullyQualifiedIdentifier(), input);
        assertFalse(statusValues.isEmpty());
        StatusValue security = statusValues.iterator().next();
        assertEquals("security", security.getField());
        assertEquals("foobar", security.getMessage());
        assertEquals(Status.ORANGE, security.getStatus());
    }

    @Test
    void isEqual() {
        //equality on field basis only to guarantee uniqueness
        assertThat(new StatusValue(foo.getFullyQualifiedIdentifier(), "bar", Status.GREEN, ""))
                .isEqualTo(new StatusValue(foo.getFullyQualifiedIdentifier(), "bar", Status.RED, ""));
    }

    @Test
    void isNotEqual() {
        assertThat(new StatusValue(foo.getFullyQualifiedIdentifier(), "bar", Status.GREEN, ""))
                .isNotEqualTo(new StatusValue(foo.getFullyQualifiedIdentifier(), "baz", Status.RED, ""));
    }

    @Test
    void toStringTest() {
        //equality on field basis only to guarantee uniqueness
        assertThat(new StatusValue(foo.getFullyQualifiedIdentifier(), "bar", Status.GREEN, "").toString())
                .hasToString("StatusValue{identifier='" + foo.getFullyQualifiedIdentifier() + "', field='bar', status=green, message='', summary=false}");
    }
}