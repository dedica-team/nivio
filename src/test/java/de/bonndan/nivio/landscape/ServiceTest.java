package de.bonndan.nivio.landscape;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class ServiceTest {

    @Test
    public void equalsWithGroup() {

        Landscape landscape = new Landscape();
        landscape.setIdentifier("l");

        Service s1 = new Service();
        s1.setIdentifier("a");
        s1.setGroup("g1");
        s1.setLandscape(landscape);

        Service s2 = new Service();
        s2.setIdentifier("a");
        s2.setGroup("g1");
        s2.setLandscape(landscape);

        Service s3 = new Service();
        s3.setIdentifier("a");
        s3.setGroup("g2");
        s3.setLandscape(landscape);

        Service s4 = new Service();
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

        Landscape landscape = new Landscape();
        landscape.setIdentifier("l");

        Service s1 = new Service();
        s1.setIdentifier("a");
        s1.setLandscape(landscape);

        Service s2 = new Service();
        s2.setIdentifier("a");
        s2.setLandscape(landscape);

        assertEquals(s1, s2);
        assertEquals(s2, s1);

        Service s3 = new Service();
        s3.setIdentifier("a");
        s3.setGroup("g2");
        s3.setLandscape(landscape);

        assertNotEquals(s1, s3);
        assertNotEquals(s2, s3);
    }

    @Test
    public void equalsWithLandscape() {

        Landscape landscape = new Landscape();
        landscape.setIdentifier("l");

        Service s1 = new Service();
        s1.setIdentifier("a");
        s1.setGroup("g1");
        s1.setLandscape(landscape);

        Service s2 = new Service();
        s2.setIdentifier("a");
        s2.setGroup("g1");
        s2.setLandscape(landscape);

        assertEquals(s1, s2);

        Service s3 = new Service();
        s3.setIdentifier("a");
        s3.setGroup("g1");
        s3.setLandscape(null);

        assertNotEquals(s1, s3);
    }
}
