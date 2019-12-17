package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.RelationItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

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
    public void fieldLabel() {
        LabelProcessor.applyLabel(item, "foo", "bar");
        LabelProcessor.applyLabel(item, "nivio.description", "baz");

        assertEquals(2, item.getLabels().size());
        assertEquals("bar", item.getLabels().get("foo"));
    }


    @Test
    public void ignoreBlacklisted() {
        LabelProcessor.applyLabel(item, "pass", "x");
        assertEquals(0, item.getLabels().size());

        LabelProcessor.applyLabel(item, "a.pass_", "x");
        assertEquals(0, item.getLabels().size());

        LabelProcessor.applyLabel(item, "secret", "x");
        assertEquals(0, item.getLabels().size());

        LabelProcessor.applyLabel(item, "my_secret_x", "x");
        assertEquals(0, item.getLabels().size());

        LabelProcessor.applyLabel(item, "credentials", "x");
        assertEquals(0, item.getLabels().size());

        LabelProcessor.applyLabel(item, "_credentials_", "x");
        assertEquals(0, item.getLabels().size());

        LabelProcessor.applyLabel(item, "token", "x");
        assertEquals(0, item.getLabels().size());

        LabelProcessor.applyLabel(item, "a_token_", "x");
        assertEquals(0, item.getLabels().size());

    }
}