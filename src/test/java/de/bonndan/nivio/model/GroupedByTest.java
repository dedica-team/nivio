package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.junit.jupiter.api.Assertions.*;

public class GroupedByTest {

    @Test
    public void testBy() {
        List<Item> items = new ArrayList<>();
        Item item1 = getTestItemBuilder("1","1").withOwner("A").build();
        items.add(item1);

        Item item2 = getTestItemBuilder("2","2").withOwner("A").build();
        items.add(item2);

        Item item3 = getTestItemBuilder("3","3").withOwner("B").build();
        items.add(item3);

        GroupedBy ownerGroups = GroupedBy.by(Item::getOwner, items);
        Map<String, List<Item>> all = ownerGroups.getAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(all.containsKey("A"));
        List<Item> a = all.get("A");
        assertEquals(2, a.size());

        List<Item> b = all.get("B");
        assertEquals(1, b.size());
    }

    @Test
    public void testByDefault() {
        List<Item> services = new ArrayList<>();
        Item item1 = getTestItem("1","1");
        services.add(item1);

        Item item2 = getTestItemBuilder("2","2").withOwner("A").build();
        services.add(item2);

        Item item3 = getTestItemBuilder("3","3").withOwner("B").build();
        services.add(item3);

        GroupedBy ownerGroups = GroupedBy.by(Item::getOwner, services);
        Map<String, List<Item>> all = ownerGroups.getAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(all.containsKey("A"));
        List<Item> a = all.get("A");
        assertEquals(1, a.size());

        List<Item> b = all.get("B");
        assertEquals(1, b.size());

        List<Item> common = all.get(Group.COMMON);
        assertEquals(1, common.size());
    }

    @Test
    public void testByNotUsingGroupField() {
        List<Item> services = new ArrayList<>();
        Item item1 = getTestItemBuilder("content","1").withOwner("A").build();
        services.add(item1);

        Item item2 = getTestItemBuilder("content","2").withOwner("A").build();
        services.add(item2);

        Item item3 = getTestItemBuilder("null","3").withOwner("B").build();
        services.add(item3);

        GroupedBy ownerGroups = GroupedBy.by(Item::getOwner, services);
        Map<String, List<Item>> all = ownerGroups.getAll();
        assertEquals(2, all.size());
        assertFalse(all.containsKey(Group.COMMON));
        assertFalse(all.containsKey("content"));
    }
}
