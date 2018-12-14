package de.bonndan.nivio.landscape;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilsTest {

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

        assertThrows(RuntimeException.class,() -> Utils.pick("s1", "xxx", services));
        assertThrows(RuntimeException.class,() -> Utils.pick("s3", "g1", services));
    }

    @Test
    public void pick() {

        assertNotNull(Utils.pick("s1", "g1", services));
        assertNotNull(Utils.pick("s2", "g1", services));

        Service s2 = new Service();
        s2.setIdentifier("s2");
        s2.setGroup("g1");
        s2.setLandscape(landscape);

        assertNotNull(Utils.pick(s2, services));
    }

    @Test
    public void pickGracefulWithoutGroup() {

        assertNotNull(Utils.pick("s2", null, services));
    }

    @Test
    public void pickGracefulFails() {

        Service s2 = new Service();
        s2.setIdentifier("s2");
        s2.setGroup("g2"); //othergroup
        s2.setLandscape(landscape);
        services.add(s2);

        assertThrows(RuntimeException.class,() -> Utils.pick("s2", null, services));
    }
}
