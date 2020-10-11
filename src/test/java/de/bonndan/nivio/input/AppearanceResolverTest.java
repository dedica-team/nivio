package de.bonndan.nivio.input;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.VendorIcons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppearanceResolverTest {

    private AppearanceResolver resolver;
    private Landscape landscape;

    @BeforeEach
    public void setup() {
        resolver = new AppearanceResolver(new ProcessLog(LoggerFactory.getLogger(AppearanceResolverTest.class)), new LocalServer("", new VendorIcons()));

        landscape = new Landscape("l1", new Group(Group.COMMON));

        Group g1 = new Group("g1");
        landscape.addGroup(g1);
        List<Item> items = new ArrayList<>();

        Item s1 = new Item("g1", "s1");
        s1.setLandscape(landscape);
        s1.setLabel(Label.type, "loadbalancer");
        items.add(s1);
        g1.addItem(s1);

        Item s2 = new Item("g1", "s2");
        s2.setLandscape(landscape);
        s2.setIcon("https://foo.bar/icon.png");
        items.add(s2);
        g1.addItem(s2);

        landscape.setItems(new HashSet<>(items));
    }

    @Test
    public void setsItemIcons() {

        resolver.process(null, landscape);

        Item pick = landscape.getItems().pick("s2", "g1");
        //check icon is set
        assertEquals("http://localhost:8080/vendoricons/aHR0cHM6Ly9mb28uYmFyL2ljb24ucG5n", pick.getIcon());

        pick = landscape.getItems().pick("s1", "g1");
        //check icon is set
        assertEquals("http://localhost:8080/icons/loadbalancer.png", pick.getIcon());
    }
}