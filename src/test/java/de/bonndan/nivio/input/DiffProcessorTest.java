package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DiffProcessorTest {

    private Landscape landscape;
    private Group g1;

    @BeforeEach
    public void setup() {

        g1 = new Group("g1", "test");
        landscape = LandscapeFactory.createForTesting("test", "l1name")
                .withGroups(Map.of("g1", g1))
                .build();

        Set<Item> items = new HashSet<>();

        Item s1 = getTestItem("g1", "s1", landscape);
        items.add(s1);

        Item s2 = getTestItem("g1", "s2", landscape);
        items.add(s2);
        landscape.setItems(items);

        g1.addOrReplaceItem(s1);
        g1.addOrReplaceItem(s2);
    }

    @Test
    void addedWithDefaultGroup() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<Item> existing = new ArrayList<>();
        existing.add(getTestItem(Layer.domain.name(), "c", landscape));

        List<ItemDescription> added = DiffProcessor.added(items1, existing);
        assertEquals(2, added.size());
    }

    @Test
    void addedWithGroup() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        ItemDescription itemDescription = new ItemDescription("c");
        itemDescription.setGroup("a");
        items1.add(itemDescription);

        ArrayList<Item> existing = new ArrayList<>();
        existing.add(getTestItem("a", "c", landscape));

        List<ItemDescription> added = DiffProcessor.added(items1, existing);
        assertEquals(2, added.size());
    }

    @Test
    void addedNone() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("a"));
        items1.add(new ItemDescription("b"));
        items1.add(new ItemDescription("c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(getTestItem(Layer.domain.name(), "a"));
        items2.add(getTestItem(Layer.domain.name(), "b"));
        items2.add(getTestItem(Layer.domain.name(), "c"));

        List<ItemDescription> added = DiffProcessor.added(items1, items2);
        assertEquals(0, added.size());
    }

    @Test
    void removed() {

        ArrayList<Item> items1 = new ArrayList<>();
        items1.add(getTestItem("a", "a"));
        items1.add(getTestItem("a", "b"));
        items1.add(getTestItem("a", "c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(getTestItem("a", "c"));
        items2.add(getTestItem("a", "d"));

        List<Item> removed = DiffProcessor.removed(items1, items2);
        assertEquals(1, removed.size());
    }

    @Test
    void removedNone() {

        ArrayList<Item> items1 = new ArrayList<>();
        items1.add(getTestItem("a", "a"));
        items1.add(getTestItem("a", "b"));
        items1.add(getTestItem("a", "c"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(getTestItem("a", "a"));
        items2.add(getTestItem("a", "b"));

        List<Item> removed = DiffProcessor.removed(items1, items2);
        assertEquals(0, removed.size());
    }

    @Test
    void kept() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("aa"));
        items1.add(new ItemDescription("bb"));
        items1.add(new ItemDescription("cc"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(getTestItem("a", "aa"));
        items2.add(getTestItem("a", "bb"));

        List<Item> kept = DiffProcessor.kept(items1, items2);
        assertEquals(2, kept.size());
    }

    @Test
    void keptNone() {

        ArrayList<ItemDescription> items1 = new ArrayList<>();
        items1.add(new ItemDescription("aa"));
        items1.add(new ItemDescription("bb"));
        items1.add(new ItemDescription("cc"));

        ArrayList<Item> items2 = new ArrayList<>();
        items2.add(getTestItem("a", "dd"));
        items2.add(getTestItem("a", "ee"));

        List<Item> kept = DiffProcessor.kept(items1, items2);
        assertEquals(0, kept.size());
    }

    @Test
    void regression439() {

        DiffProcessor diffProcessor = new DiffProcessor(new ProcessLog(LoggerFactory.getLogger(DiffProcessorTest.class), landscape.getIdentifier()));

        LandscapeDescription input = new LandscapeDescription(landscape.getIdentifier());
        ItemDescription description = new ItemDescription("s1");
        description.setGroup("g1");
        input.setItems(List.of(description));

        //when
        diffProcessor.process(input, landscape);

        //then
        assertThat(g1.getItems()).hasSize(1);
    }
}
