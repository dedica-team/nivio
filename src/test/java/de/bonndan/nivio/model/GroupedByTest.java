package de.bonndan.nivio.model;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
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

        Item item2 = getTestItemBuilder("2","2").withOwner("B").build();
        items.add(item2);

        Item item3 = getTestItemBuilder("3","3").withOwner("B").build();
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
        Item item4 = getTestItemBuilder("1","1").withGroup("A").build();
        newItems.add(item4);
        Item item5 = getTestItemBuilder("2","2").withGroup("B").build();
        newItems.add(item5);
        Item item6 = getTestItemBuilder("3","3").withGroup("C").build();
        newItems.add(item6);


        GroupedBy groupGroups = GroupedBy.by(item-> item.getGroup(),newItems);
        Map<String, List<Item>> all1 = groupGroups.getAll();
        assertNotNull(all1);
        assertFalse(all1.isEmpty());
        assertTrue(all1.containsKey("A"));
        assertTrue(all1.containsKey("B"));
        assertTrue(all1.containsKey("C"));
        List<Item> a1 = all1.get("A");
        assertEquals(1,a1.size());
        List<Item> a2 = all1.get("B");
        assertEquals(1,a2.size());
        List<Item> a3 = all1.get("C");
        assertEquals(1,a3.size());

        Map<String,String> labels = new HashMap<>();
        labels.put("A","1");
        List<Item> labelsItems = new ArrayList<>();
        Item item7 = getTestItemBuilder("A","1").withLabels(labels).build();
        labels.put("B","2");
        labelsItems.add(item7);
        Item item8 = getTestItemBuilder("B","2").withLabels(labels).build();
        labelsItems.add(item8);
        labels.put("C","3");
        Item item9 = getTestItemBuilder("C","3").withLabels(labels).build();
        labelsItems.add(item9);


        GroupedBy labelsGroups = GroupedBy.newBy(item->item.getLabels(),labelsItems);
        Map<String,List<Item>> all2 = labelsGroups.getAll();
        assertNotNull(all2);
        assertFalse(all2.isEmpty());
        assertFalse(all2.containsKey("A"));
        assertFalse(all2.containsValue("A"));
        assertTrue(all2.containsKey("common"));

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

        List<Item> newServices = new ArrayList<>();
        Item item4 = getTestItem("1","1");
        newServices.add(item4);
        Item item5 = getTestItemBuilder("2","2").withGroup("A").build();
        newServices.add(item5);
        Item item6 = getTestItemBuilder("3","3").withGroup("B").build();
        newServices.add(item6);

        GroupedBy groupGroups = GroupedBy.by(item->item.getGroup(), newServices);
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

        Map<String,String> labels = new HashMap<>();
        List<Item>labelsItems = new ArrayList<>();
        Item item7 = getTestItem("A","1");
        labelsItems.add(item7);
        labels.put("X","a");
        Item item8 = getTestItemBuilder("B","2").withLabels(labels).build();
        labelsItems.add(item8);
        labels.put("Y","b");
        Item item9 = getTestItemBuilder("C","3").withLabels(labels).build();
        labelsItems.add(item9);

        GroupedBy labelsGroups = GroupedBy.newBy(item->item.getLabels(),labelsItems);
        Map<String,List<Item>> all2 = labelsGroups.getAll();
        assertNotNull(all2);
        assertFalse(all2.isEmpty());
        assertFalse(all2.containsKey("X"));
        assertFalse(all2.containsKey("B"));
        assertFalse(all2.containsValue("a"));
        assertTrue(all2.containsKey("common"));
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

        List<Item> newServices = new ArrayList<>();
        Item item4 = getTestItemBuilder("content","1").withGroup("A").build();
        newServices.add(item4);

        Item item5 = getTestItemBuilder("content","2").withGroup("B").build();
        newServices.add(item5);

        Item item6 = getTestItemBuilder("null","3").withGroup("C").build();
        newServices.add(item6);

        GroupedBy groupGroups = GroupedBy.by(item->item.getGroup(), newServices);
        Map<String, List<Item>> all1 = groupGroups.getAll();
        assertEquals(3, all1.size());
        assertFalse(all1.containsKey(Group.COMMON));
        assertFalse(all1.containsKey("content"));

        Map<String,String> labels = new HashMap<>();
        List<Item> labelsItems = new ArrayList<>();
        labels.put("X","a");
        Item item7 = getTestItemBuilder("content","1").withLabels(labels).build();
        labelsItems.add(item7);
        labels.put("Y","b");
        Item item8 = getTestItemBuilder("content","2").withLabels(labels).build();
        labelsItems.add(item8);
        labels.put("Z","c");
        Item item9 = getTestItemBuilder("null","3").withLabels(labels).build();

        GroupedBy labelsGroups = GroupedBy.newBy(item->item.getLabels(),labelsItems);
        Map<String,List<Item>> all2 = labelsGroups.getAll();
        assertEquals(1,all2.size());
        assertTrue(all2.containsKey(Group.COMMON));
        assertFalse(all2.containsKey("content"));
    }
}
