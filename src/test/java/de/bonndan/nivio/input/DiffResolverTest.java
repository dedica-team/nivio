package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiffResolverTest {

    private ArrayList<Item> items;
    private LandscapeImpl landscape;

    @BeforeEach
    public void setup() {

        landscape = LandscapeFactory.create("l1");

        items = new ArrayList<>();

        Item s1 = new Item("g1","s1");
        s1.setLandscape(landscape);
        items.add(s1);

        Item s2 = new Item("g1","s2");
        s2.setLandscape(landscape);
        items.add(s2);
    }

    @Test
    public void added() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(new Item("a", "c"));

        List<Item> added = DiffResolver.added(items1, items2, landscape);
        assertEquals(2, added.size());
    }

    @Test
    public void addedNone() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(new Item("a", "a"));
        items2.add(new Item("a", "b"));
        items2.add(new Item("a", "c"));

        List<Item> added = DiffResolver.added(items1, items2, landscape);
        assertEquals(0, added.size());
    }

    @Test
    public void removed() {

        ArrayList<Item> items1 = new ArrayList<>();
        items1.add(new Item("a", "a"));
        items1.add(new Item("a", "b"));
        items1.add(new Item("a", "c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(new Item("a", "c"));
        items2.add(new Item("a", "d"));

        List<Item> removed = DiffResolver.removed(items1, items2);
        assertEquals(1, removed.size());
    }

    @Test
    public void removedNone() {

        ArrayList<Item> items1 = new ArrayList<>();
        items1.add(new Item("a", "a"));
        items1.add(new Item("a", "b"));
        items1.add(new Item("a", "c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(new Item("a", "a"));
        items2.add(new Item("a", "b"));

        List<Item> removed = DiffResolver.removed(items1, items2);
        assertEquals(0, removed.size());
    }

    @Test
    public void kept() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(new Item("a", "a"));
        items2.add(new Item("a", "b"));

        List<Item> kept = DiffResolver.kept(items1, items2, landscape);
        assertEquals(2, kept.size());
    }

    @Test
    public void keptNone() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(new Item("a", "d"));
        items2.add(new Item("a", "e"));

        List<Item> kept = DiffResolver.kept(items1, items2, landscape);
        assertEquals(0, kept.size());
    }
}
