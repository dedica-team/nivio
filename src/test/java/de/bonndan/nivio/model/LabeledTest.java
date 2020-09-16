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
        all.put(Label.costs.name(), "0");
        all.put(Label.note.name(), "foo");
        all.put(Label.network + Label.DELIMITER + "x", "x");
        all.put(Label.network + Label.DELIMITER + "y", "y");
        all.put(Label.network + Label.DELIMITER + "z", "z");

        Map<String, String> groupedByPrefixes = Labeled.groupedByPrefixes(all);
        assertNotNull(groupedByPrefixes);
        assertEquals(3, groupedByPrefixes.size());
        assertEquals("0", groupedByPrefixes.get(Label.costs.name()));
        assertEquals("foo", groupedByPrefixes.get(Label.note.name()));
        assertTrue(groupedByPrefixes.get(Label.network.name()).contains("x"));
        assertTrue(groupedByPrefixes.get(Label.network.name()).contains("y"));
        assertTrue(groupedByPrefixes.get(Label.network.name()).contains("z"));
    }

    @Test
    public void setPrefixed() {
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setPrefixed(Label.network, "foo");
        assertEquals(1, itemDescription.getLabels(Label.network).size());
        assertTrue(itemDescription.getLabels().containsKey(Label.network + ".foo"));
        String label = itemDescription.getLabel(Label.network + ".foo");
        assertEquals("foo", label);
    }
}