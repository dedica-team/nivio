package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.model.ServiceItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LandscapeItemsTest {

    private ArrayList<Item> items;
    private LandscapeImpl landscape;

    @BeforeEach
    public void setup() {

        landscape = new LandscapeImpl();
        landscape.setIdentifier("l");

        items = new ArrayList<>();

        Item s1 = new Item();
        s1.setIdentifier("s1");
        s1.setGroup("g1");
        s1.setLandscape(landscape);
        items.add(s1);

        Item s2 = new Item();
        s2.setIdentifier("s2");
        s2.setGroup("g1");
        s2.setLandscape(landscape);
        items.add(s2);
    }

    @Test
    public void pickFails() {

        assertThrows(RuntimeException.class,() -> ServiceItems.pick("s1", "xxx", items));
        assertThrows(RuntimeException.class,() -> ServiceItems.pick("s3", "g1", items));
    }

    @Test
    public void pick() {

        assertNotNull(ServiceItems.pick("s1", "g1", items));
        assertNotNull(ServiceItems.pick("s2", "g1", items));

        Item s2 = new Item();
        s2.setIdentifier("s2");
        s2.setGroup("g1");
        s2.setLandscape(landscape);

        assertNotNull(ServiceItems.pick(s2, items));
    }

    @Test
    public void pickGracefulWithoutGroup() {

        assertNotNull(ServiceItems.pick("s2", null, items));
    }

    @Test
    public void pickGracefulFails() {

        Item s2 = new Item();
        s2.setIdentifier("s2");
        s2.setGroup("g2"); //othergroup
        s2.setLandscape(landscape);
        items.add(s2);

        assertThrows(RuntimeException.class,() -> ServiceItems.pick("s2", null, items));
    }
    @Test
    public void added() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ItemDescription("c"));

        List<LandscapeItem> added = Indexer.added(items1, items2);
        assertEquals(2, added.size());
    }

    @Test
    public void addedNone() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ItemDescription("a"));
        items2.add(new ItemDescription("b"));
        items2.add(new ItemDescription("c"));

        List<LandscapeItem> added = Indexer.added(items1, items2);
        assertEquals(0, added.size());
    }

    @Test
    public void removed() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ItemDescription("c"));
        items2.add(new ItemDescription("d"));

        List<LandscapeItem> removed = Indexer.removed(items1, items2);
        assertEquals(1, removed.size());
    }

    @Test
    public void removedNone() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ItemDescription("a"));
        items2.add(new ItemDescription("b"));

        List<LandscapeItem> removed = Indexer.removed(items1, items2);
        assertEquals(0, removed.size());
    }

    @Test
    public void kept() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ItemDescription("a"));
        items2.add(new ItemDescription("b"));

        List<LandscapeItem> kept = Indexer.kept(items1, items2);
        assertEquals(2, kept.size());
    }

    @Test
    public void keptNone() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ItemDescription("d"));
        items2.add(new ItemDescription("e"));

        List<LandscapeItem> kept = Indexer.kept(items1, items2);
        assertEquals(0, kept.size());
    }
}
