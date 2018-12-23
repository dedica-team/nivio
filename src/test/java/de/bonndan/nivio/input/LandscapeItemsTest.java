package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.landscape.ServiceItems;
import de.bonndan.nivio.landscape.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LandscapeItemsTest {

    private ArrayList<Service> services;
    private Landscape landscape;

    @BeforeEach
    public void setup() {

        landscape = new Landscape();
        landscape.setIdentifier("l");

        services = new ArrayList<>();

        Service s1 = new Service();
        s1.setIdentifier("s1");
        s1.setGroup("g1");
        s1.setLandscape(landscape);
        services.add(s1);

        Service s2 = new Service();
        s2.setIdentifier("s2");
        s2.setGroup("g1");
        s2.setLandscape(landscape);
        services.add(s2);
    }

    @Test
    public void pickFails() {

        assertThrows(RuntimeException.class,() -> ServiceItems.pick("s1", "xxx", services));
        assertThrows(RuntimeException.class,() -> ServiceItems.pick("s3", "g1", services));
    }

    @Test
    public void pick() {

        assertNotNull(ServiceItems.pick("s1", "g1", services));
        assertNotNull(ServiceItems.pick("s2", "g1", services));

        Service s2 = new Service();
        s2.setIdentifier("s2");
        s2.setGroup("g1");
        s2.setLandscape(landscape);

        assertNotNull(ServiceItems.pick(s2, services));
    }

    @Test
    public void pickGracefulWithoutGroup() {

        assertNotNull(ServiceItems.pick("s2", null, services));
    }

    @Test
    public void pickGracefulFails() {

        Service s2 = new Service();
        s2.setIdentifier("s2");
        s2.setGroup("g2"); //othergroup
        s2.setLandscape(landscape);
        services.add(s2);

        assertThrows(RuntimeException.class,() -> ServiceItems.pick("s2", null, services));
    }
    @Test
    public void added() {

        ArrayList<ServiceItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<ServiceItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("c"));

        List<ServiceItem> added = ServiceItems.added(items1, items2);
        assertEquals(2, added.size());
    }

    @Test
    public void addedNone() {

        ArrayList<ServiceItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<ServiceItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("a"));
        items2.add(new ServiceDescription("b"));
        items2.add(new ServiceDescription("c"));

        List<ServiceItem> added = ServiceItems.added(items1, items2);
        assertEquals(0, added.size());
    }

    @Test
    public void removed() {

        ArrayList<ServiceItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<ServiceItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("c"));
        items2.add(new ServiceDescription("d"));

        List<ServiceItem> removed = ServiceItems.removed(items1, items2);
        assertEquals(1, removed.size());
    }

    @Test
    public void removedNone() {

        ArrayList<ServiceItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<ServiceItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("a"));
        items2.add(new ServiceDescription("b"));

        List<ServiceItem> removed = ServiceItems.removed(items1, items2);
        assertEquals(0, removed.size());
    }

    @Test
    public void kept() {

        ArrayList<ServiceItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<ServiceItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("a"));
        items2.add(new ServiceDescription("b"));

        List<ServiceItem> kept = ServiceItems.kept(items1, items2);
        assertEquals(2, kept.size());
    }

    @Test
    public void keptNone() {

        ArrayList<ServiceItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<ServiceItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("d"));
        items2.add(new ServiceDescription("e"));

        List<ServiceItem> kept = ServiceItems.kept(items1, items2);
        assertEquals(0, kept.size());
    }
}
