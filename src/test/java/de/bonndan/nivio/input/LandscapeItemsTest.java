package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.landscape.LandscapeItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LandscapeItemsTest {

    @Test
    public void added() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("c"));

        List<LandscapeItem> added = LandscapeItems.added(items1, items2);
        assertEquals(2, added.size());
    }

    @Test
    public void addedNone() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("a"));
        items2.add(new ServiceDescription("b"));
        items2.add(new ServiceDescription("c"));

        List<LandscapeItem> added = LandscapeItems.added(items1, items2);
        assertEquals(0, added.size());
    }

    @Test
    public void removed() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("c"));
        items2.add(new ServiceDescription("d"));

        List<LandscapeItem> removed = LandscapeItems.removed(items1, items2);
        assertEquals(1, removed.size());
    }

    @Test
    public void removedNone() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("a"));
        items2.add(new ServiceDescription("b"));

        List<LandscapeItem> removed = LandscapeItems.removed(items1, items2);
        assertEquals(0, removed.size());
    }

    @Test
    public void kept() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("a"));
        items2.add(new ServiceDescription("b"));

        List<LandscapeItem> kept = LandscapeItems.kept(items1, items2);
        assertEquals(2, kept.size());
    }

    @Test
    public void keptNone() {

        ArrayList<LandscapeItem> items1 = new ArrayList<>();
        items1.add(new ServiceDescription("a"));
        items1.add(new ServiceDescription("b"));
        items1.add(new ServiceDescription("c"));

        ArrayList<LandscapeItem> items2 = new ArrayList<>();
        items2.add(new ServiceDescription("d"));
        items2.add(new ServiceDescription("e"));

        List<LandscapeItem> kept = LandscapeItems.kept(items1, items2);
        assertEquals(0, kept.size());
    }
}
