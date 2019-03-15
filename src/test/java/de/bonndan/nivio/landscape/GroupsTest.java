package de.bonndan.nivio.landscape;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class GroupsTest {

    @Test
    public void testBy() {
        List<Service> services = new ArrayList<>();
        Service service1 = new Service();
        service1.setIdentifier("1");
        service1.setOwner("A");
        services.add(service1);

        Service service2 = new Service();
        service2.setIdentifier("2");
        service2.setOwner("A");
        services.add(service2);

        Service service3 = new Service();
        service3.setIdentifier("3");
        service3.setOwner("B");
        services.add(service3);

        Groups ownerGroups = Groups.by(ServiceItem::getOwner, services);
        Map<String, List<ServiceItem>> all = ownerGroups.getAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(all.containsKey("A"));
        List<ServiceItem> a = all.get("A");
        assertEquals(2, a.size());

        List<ServiceItem> b = all.get("B");
        assertEquals(1, b.size());
    }

    @Test
    public void testByDefault() {
        List<Service> services = new ArrayList<>();
        Service service1 = new Service();
        service1.setIdentifier("1");
        services.add(service1);

        Service service2 = new Service();
        service2.setIdentifier("2");
        service2.setOwner("A");
        services.add(service2);

        Service service3 = new Service();
        service3.setIdentifier("3");
        service3.setOwner("B");
        services.add(service3);

        Groups ownerGroups = Groups.by(ServiceItem::getOwner, services);
        Map<String, List<ServiceItem>> all = ownerGroups.getAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
        assertTrue(all.containsKey("A"));
        List<ServiceItem> a = all.get("A");
        assertEquals(1, a.size());

        List<ServiceItem> b = all.get("B");
        assertEquals(1, b.size());

        List<ServiceItem> common = all.get(Groups.COMMON);
        assertEquals(1, common.size());
    }

    @Test
    public void testByNotUsingGroupField() {
        List<Service> services = new ArrayList<>();
        Service service1 = new Service();
        service1.setIdentifier("1");
        service1.setOwner("A");
        service1.setGroup("content");
        services.add(service1);

        Service service2 = new Service();
        service2.setIdentifier("2");
        service2.setOwner("A");
        service2.setGroup("content");
        services.add(service2);

        Service service3 = new Service();
        service3.setIdentifier("3");
        service3.setOwner("B");
        services.add(service3);

        Groups ownerGroups = Groups.by(ServiceItem::getOwner, services);
        Map<String, List<ServiceItem>> all = ownerGroups.getAll();
        assertEquals(2, all.size());
        assertFalse(all.containsKey(Groups.COMMON));
        assertFalse(all.containsKey("content"));
    }
}
