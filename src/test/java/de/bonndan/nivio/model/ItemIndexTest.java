package de.bonndan.nivio.model;

import de.bonndan.nivio.input.ItemDescriptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemIndexTest {

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

        Item s2 = new Item();
        s2.setIdentifier("s2");
        s2.setGroup("g1");
        s2.setLandscape(landscape);

        assertNotNull(landscape.getItems().pick(s2));
    }

    @Test
    public void pickGracefulWithoutGroup() {

        assertNotNull(landscape.getItems().pick("s2", null));
    }

    @Test
    public void pickGracefulFails() {

        Item s2 = new Item();
        s2.setIdentifier("s2");
        s2.setGroup("g2"); //othergroup
        s2.setLandscape(landscape);
        items.add(s2);
        landscape.setItems(new HashSet<>(items));

        assertThrows(RuntimeException.class,() -> landscape.getItems().pick("s2", null));
    }
}