package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemTest {

    @Test
    public void equalsWithGroup() {

        LandscapeImpl landscape = LandscapeFactory.create("l1");


        Item s1 = new Item("g1","a");
        s1.setLandscape(landscape);

        Item s2 = new Item("g1","a");
        s2.setLandscape(landscape);

        Item s3 = new Item("g2","a");
        s3.setLandscape(landscape);

        Item s4 = new Item(null, "a");
        s4.setLandscape(landscape);

        assertEquals(s1, s2);
        assertEquals(s2, s1);
        assertNotEquals(s3, s1);
        assertNotEquals(s3, s2);
        assertNotEquals(s4, s1);
        assertNotEquals(s4, s2);
    }

    @Test
    public void equalsWithoutGroup() {

        LandscapeImpl landscape = LandscapeFactory.create("l1");

        Item s1 = new Item(null, "a");
        s1.setLandscape(landscape);

        Item s2 = new Item(null, "a");
        s2.setLandscape(landscape);

        assertEquals(s1, s2);
        assertEquals(s2, s1);

        Item s3 = new Item("g2", "a");
        s3.setLandscape(landscape);

        assertNotEquals(s1, s3);
        assertNotEquals(s2, s3);
    }

    @Test
    public void equalsWithLandscape() {

        LandscapeImpl landscape = LandscapeFactory.create("l1");

        Item s1 = new Item("g1", "a");
        s1.setLandscape(landscape);

        Item s2 = new Item("g1", "a");
        s2.setLandscape(landscape);

        assertEquals(s1, s2);

        Item s3 = new Item("g1", "a");
        s3.setLandscape(null);

        assertNotEquals(s1, s3);
    }

    @Test
    void setGroupIfNull() {
        Item a = new Item(null, "a");
        a.setGroup("b");
        assertEquals("b", a.getGroup());
    }

    @Test
    void setGroupForbidden() {
        Item a = new Item("b", "a");
        assertThrows(IllegalArgumentException.class, ()->a.setGroup("c"));
    }
}
