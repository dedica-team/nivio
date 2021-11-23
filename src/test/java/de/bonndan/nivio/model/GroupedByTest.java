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
        Item item1 = getTestItemBuilder("1", "1").withOwner("A").build();
        items.add(item1);

        Item item2 = getTestItemBuilder("2", "2").withOwner("B").build();
        items.add(item2);

        Item item3 = getTestItemBuilder("3", "3").withOwner("B").build();
        items.add(item3);


        GroupedBy ownerGroups = GroupedBy.by(Item::getOwner, items);
        Map<String, List<Item>> all = ownerGroups.getAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(all.containsKey("A"));
        assertTrue(all.containsKey("B"));
        List<Item> a = all.get("A");
        assertEquals(1, a.size());
        List<Item> b = all.get("B");
        assertEquals(2, b.size());


        List<Item> newItems = new ArrayList<>();
        Item item4 = getTestItemBuilder("1", "1").withGroup("A").build();
        newItems.add(item4);
        Item item5 = getTestItemBuilder("2", "2").withGroup("B").build();
        newItems.add(item5);
        Item item6 = getTestItemBuilder("3", "3").withGroup("C").build();
        newItems.add(item6);


        GroupedBy groupGroups = GroupedBy.by(item -> item.getGroup(), newItems);
        Map<String, List<Item>> all1 = groupGroups.getAll();
        assertNotNull(all1);
        assertFalse(all1.isEmpty());
        assertTrue(all1.containsKey("A"));
        assertTrue(all1.containsKey("B"));
        assertTrue(all1.containsKey("C"));
        List<Item> a1 = all1.get("A");
        assertEquals(1, a1.size());
        List<Item> a2 = all1.get("B");
        assertEquals(1, a2.size());
        List<Item> a3 = all1.get("C");
        assertEquals(1, a3.size());
    }

    @Test
    public void testByDefault() {
        List<Item> services = new ArrayList<>();
        Item item1 = getTestItem("1", "1");
        services.add(item1);

        Item item2 = getTestItemBuilder("2", "2").withOwner("A").build();
        services.add(item2);

        Item item3 = getTestItemBuilder("3", "3").withOwner("B").build();
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

        List<Item> newServices = new ArrayList<>();
        Item item4 = getTestItem("1", "1");
        newServices.add(item4);
        Item item5 = getTestItemBuilder("2", "2").withGroup("A").build();
        newServices.add(item5);
        Item item6 = getTestItemBuilder("3", "3").withGroup("B").build();
        newServices.add(item6);

        GroupedBy groupGroups = GroupedBy.by(item -> item.getGroup(), newServices);
        Map<String, List<Item>> all1 = groupGroups.getAll();
        assertNotNull(all1);
        assertFalse(all1.isEmpty());
        assertTrue(all1.containsKey("A"));
        assertTrue(all1.containsKey("B"));
        assertFalse(all1.containsKey("C"));
        List<Item> a1 = all.get("A");
        assertEquals(1, a1.size());
        List<Item> b1 = all.get("B");
        assertEquals(1, b1.size());
        List<Item> newCommon = all.get(Group.COMMON);
        assertEquals(1, newCommon.size());
    }

    @Test
    public void testByNotUsingGroupField() {
        List<Item> services = new ArrayList<>();
        Item item1 = getTestItemBuilder("content", "1").withOwner("A").build();
        services.add(item1);

        Item item2 = getTestItemBuilder("content", "2").withOwner("A").build();
        services.add(item2);

        Item item3 = getTestItemBuilder("null", "3").withOwner("B").build();
        services.add(item3);

        GroupedBy ownerGroups = GroupedBy.by(Item::getOwner, services);
        Map<String, List<Item>> all = ownerGroups.getAll();
        assertEquals(2, all.size());
        assertFalse(all.containsKey(Group.COMMON));
        assertFalse(all.containsKey("content"));

        List<Item> newServices = new ArrayList<>();
        Item item4 = getTestItemBuilder("content", "1").withGroup("A").build();
        newServices.add(item4);

        Item item5 = getTestItemBuilder("content", "2").withGroup("B").build();
        newServices.add(item5);

        Item item6 = getTestItemBuilder("null", "3").withGroup("C").build();
        newServices.add(item6);

        GroupedBy groupGroups = GroupedBy.by(item -> item.getGroup(), newServices);
        Map<String, List<Item>> all1 = groupGroups.getAll();
        assertEquals(3, all1.size());
        assertFalse(all1.containsKey(Group.COMMON));
        assertFalse(all1.containsKey("content"));
    }
}
