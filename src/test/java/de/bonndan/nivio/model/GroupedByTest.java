package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GroupedByTest {

    @Test
    public void testBy() {
        List<Item> items = new ArrayList<>();
        Item item1 = new Item(null,"1");
        item1.setOwner("A");
        items.add(item1);

        Item item2 = new Item(null,"2");
        item2.setOwner("A");
        items.add(item2);

        Item item3 = new Item(null,"3");
        item3.setOwner("B");
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
        Item item1 = new Item(null,"1");
        services.add(item1);

        Item item2 = new Item(null,"2");
        item2.setOwner("A");
        services.add(item2);

        Item item3 = new Item(null,"3");
        item3.setOwner("B");
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
        Item item1 = new Item(null,"1");
        item1.setOwner("A");
        item1.setGroup("content");
        services.add(item1);

        Item item2 = new Item(null,"2");
        item2.setOwner("A");
        item2.setGroup("content");
        services.add(item2);

        Item item3 = new Item(null,"3");
        item3.setOwner("B");
        services.add(item3);

        GroupedBy ownerGroups = GroupedBy.by(Item::getOwner, services);
        Map<String, List<Item>> all = ownerGroups.getAll();
        assertEquals(2, all.size());
        assertFalse(all.containsKey(Group.COMMON));
        assertFalse(all.containsKey("content"));
    }
}
