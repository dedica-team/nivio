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

    @Test
    public void listFieldLabel() {
        LabelProcessor.applyLabel(item, "nivio.providedBy", "bar , baz ");

        List<String> providedBy = item.getProvidedBy();
        assertEquals("bar", providedBy.get(0));
        assertEquals("baz", providedBy.get(1));
    }

    @Test
    public void relations() {
        LabelProcessor.applyLabel(item, "nivio.relations", "bar , baz ");

        Set<RelationItem<String>> relations = item.getRelations();
        assertEquals(2, relations.size());
        RelationItem<String> next = relations.iterator().next();
        assertTrue("bar".equals(next.getTarget()) || "baz".equals(next.getTarget()));
        next = relations.iterator().next();
        assertTrue("bar".equals(next.getTarget()) || "baz".equals(next.getTarget()));
    }
}