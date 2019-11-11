package de.bonndan.nivio.input.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LabelProcessorTest {

    ItemDescription item;

    @BeforeEach
    public void setup() {
        item = new ItemDescription();
    }

    @Test
    public void regularLabels() {
        LabelProcessor.applyLabel(item, "foo", "bar");
        LabelProcessor.applyLabel(item, "niviofoo", "baz");

        assertEquals(2, item.getLabels().size());
        assertEquals("bar", item.getLabels().get("foo"));
        assertEquals("baz", item.getLabels().get("niviofoo"));
    }

    @Test
    public void noField() {
        LabelProcessor.applyLabel(item, "foo", "bar");
        LabelProcessor.applyLabel(item, "nivio.foo", "baz");

        assertEquals(1, item.getLabels().size());
        assertEquals("bar", item.getLabels().get("foo"));
        assertNull(item.getLabels().get("nivio.foo"));
    }

    @Test
    public void fieldLabel() {
        LabelProcessor.applyLabel(item, "foo", "bar");
        LabelProcessor.applyLabel(item, "nivio.description", "baz");

        assertEquals(1, item.getLabels().size());
        assertEquals("bar", item.getLabels().get("foo"));
        assertEquals("baz", item.getDescription());
    }
}