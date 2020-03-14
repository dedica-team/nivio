package de.bonndan.nivio.model;

import de.bonndan.nivio.input.dto.ItemDescription;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LabeledTest {

    @Test
    void groupedByPrefixes() {
        Map<String, String> all = new HashMap<>();
        all.put(Label.COSTS.name(), "0");
        all.put(Label.NOTE.name(), "foo");
        all.put(Label.PREFIX_NETWORK + Labeled.PREFIX_DELIMITER + "x", "x");
        all.put(Label.PREFIX_NETWORK + Labeled.PREFIX_DELIMITER + "y", "y");
        all.put(Label.PREFIX_NETWORK + Labeled.PREFIX_DELIMITER + "z", "z");

        Map<String, String> groupedByPrefixes = Labeled.groupedByPrefixes(all);
        assertNotNull(groupedByPrefixes);
        assertEquals(3, groupedByPrefixes.size());
        assertEquals("0", groupedByPrefixes.get(Label.COSTS.name()));
        assertEquals("foo", groupedByPrefixes.get(Label.NOTE.name()));
        assertTrue(groupedByPrefixes.get(Label.PREFIX_NETWORK).contains("x"));
        assertTrue(groupedByPrefixes.get(Label.PREFIX_NETWORK).contains("y"));
        assertTrue(groupedByPrefixes.get(Label.PREFIX_NETWORK).contains("z"));
    }

    @Test
    public void setPrefixed() {
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setPrefixed(Label.PREFIX_NETWORK, "foo");
        assertEquals(1, itemDescription.getLabels(Label.PREFIX_NETWORK ).size());
        assertTrue(itemDescription.getLabels().containsKey(Label.PREFIX_NETWORK + ".foo"));
        String label = itemDescription.getLabel(Label.PREFIX_NETWORK + ".foo");
        assertEquals("foo", label);
    }
}