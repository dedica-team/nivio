package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class GroupsTest {

    @Test
    public void testBy() {
        List<LandscapeItem> services = new ArrayList<>();
        Item item1 = new Item();
        item1.setIdentifier("1");
        item1.setOwner("A");
        services.add(item1);

        Item item2 = new Item();
        item2.setIdentifier("2");
        item2.setOwner("A");
        services.add(item2);

        Item item3 = new Item();
        item3.setIdentifier("3");
        item3.setOwner("B");
        services.add(item3);

        Groups ownerGroups = Groups.by(LandscapeItem::getOwner, services);
        Map<String, List<LandscapeItem>> all = ownerGroups.getAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(all.containsKey("A"));
        List<LandscapeItem> a = all.get("A");
        assertEquals(2, a.size());

        List<LandscapeItem> b = all.get("B");
        assertEquals(1, b.size());
    }

    @Test
    public void testByDefault() {
        List<LandscapeItem> services = new ArrayList<>();
        Item item1 = new Item();
        item1.setIdentifier("1");
        services.add(item1);

        Item item2 = new Item();
        item2.setIdentifier("2");
        item2.setOwner("A");
        services.add(item2);

        Item item3 = new Item();
        item3.setIdentifier("3");
        item3.setOwner("B");
        services.add(item3);

        Groups ownerGroups = Groups.by(LandscapeItem::getOwner, services);
        Map<String, List<LandscapeItem>> all = ownerGroups.getAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(all.containsKey("A"));
        List<LandscapeItem> a = all.get("A");
        assertEquals(1, a.size());

        List<LandscapeItem> b = all.get("B");
        assertEquals(1, b.size());

        List<LandscapeItem> common = all.get(Groups.COMMON);
        assertEquals(1, common.size());
    }

    @Test
    public void testByNotUsingGroupField() {
        List<LandscapeItem> services = new ArrayList<>();
        Item item1 = new Item();
        item1.setIdentifier("1");
        item1.setOwner("A");
        item1.setGroup("content");
        services.add(item1);

        Item item2 = new Item();
        item2.setIdentifier("2");
        item2.setOwner("A");
        item2.setGroup("content");
        services.add(item2);

        Item item3 = new Item();
        item3.setIdentifier("3");
        item3.setOwner("B");
        services.add(item3);

        Groups ownerGroups = Groups.by(LandscapeItem::getOwner, services);
        Map<String, List<LandscapeItem>> all = ownerGroups.getAll();
        assertEquals(2, all.size());
        assertFalse(all.containsKey(Groups.COMMON));
        assertFalse(all.containsKey("content"));
    }
}
