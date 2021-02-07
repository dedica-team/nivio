package de.bonndan.nivio.search;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemIndexTest {

    private ArrayList<Item> items;
    private Landscape landscape;

    @BeforeEach
    public void setup() {

        landscape = new Landscape("l1", new Group(Group.COMMON));

        items = new ArrayList<>();

        Item s1 = new Item("g1", "s1");
        s1.setName("foo");
        s1.setLandscape(landscape);
        items.add(s1);

        Item s2 = new Item("g1", "s2");
        s2.setName("bar");
        s2.setLandscape(landscape);
        items.add(s2);

        landscape.setItems(new HashSet<>(items));
    }

    @Test
    public void pickFails() {

        assertThrows(RuntimeException.class,() -> landscape.getItems().pick("s1", "xxx"));
        assertThrows(RuntimeException.class,() -> landscape.getItems().pick("s3", "g1"));
    }


    @Test
    public void pick() {
        assertNotNull(landscape.getItems().pick("s1", "g1"));
        assertNotNull(landscape.getItems().pick("s2", "g1"));

        ItemDescription s2 = new ItemDescription("s2");

        assertNotNull(landscape.getItems().pick(s2));
    }

    @Test
    public void pickGracefulWithoutGroup() {

        assertNotNull(landscape.getItems().pick("s2", null));
    }

    @Test
    public void pickGracefulFails() {

        Item s2 = new Item("g2", "s2");
        s2.setLandscape(landscape);
        items.add(s2);
        landscape.setItems(new HashSet<>(items));

        assertThrows(RuntimeException.class,() -> landscape.getItems().pick("s2", null));
    }

    @Test
    public void searchStartingWithWildcard() {
        landscape.getItems().indexForSearch();
        Set<Item> search = landscape.getItems().search("*oo");
        assertEquals(1, search.size());
    }
}
