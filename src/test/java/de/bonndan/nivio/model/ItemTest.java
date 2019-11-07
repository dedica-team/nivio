package de.bonndan.nivio.model;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class ItemTest {

    @Test
    public void equalsWithGroup() {

        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("l");

        Item s1 = new Item();
        s1.setIdentifier("a");
        s1.setGroup("g1");
        s1.setLandscape(landscape);

        Item s2 = new Item();
        s2.setIdentifier("a");
        s2.setGroup("g1");
        s2.setLandscape(landscape);

        Item s3 = new Item();
        s3.setIdentifier("a");
        s3.setGroup("g2");
        s3.setLandscape(landscape);

        Item s4 = new Item();
        s4.setIdentifier("a");
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

        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("l");

        Item s1 = new Item();
        s1.setIdentifier("a");
        s1.setLandscape(landscape);

        Item s2 = new Item();
        s2.setIdentifier("a");
        s2.setLandscape(landscape);

        assertEquals(s1, s2);
        assertEquals(s2, s1);

        Item s3 = new Item();
        s3.setIdentifier("a");
        s3.setGroup("g2");
        s3.setLandscape(landscape);

        assertNotEquals(s1, s3);
        assertNotEquals(s2, s3);
    }

    @Test
    public void equalsWithLandscape() {

        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("l");

        Item s1 = new Item();
        s1.setIdentifier("a");
        s1.setGroup("g1");
        s1.setLandscape(landscape);

        Item s2 = new Item();
        s2.setIdentifier("a");
        s2.setGroup("g1");
        s2.setLandscape(landscape);

        assertEquals(s1, s2);

        Item s3 = new Item();
        s3.setIdentifier("a");
        s3.setGroup("g1");
        s3.setLandscape(null);

        assertNotEquals(s1, s3);
    }
}
