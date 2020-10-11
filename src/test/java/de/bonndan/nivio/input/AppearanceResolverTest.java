package de.bonndan.nivio.input;

import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.icons.DataUrlHelper;
import de.bonndan.nivio.output.icons.LocalIcons;
import de.bonndan.nivio.output.icons.VendorIcons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AppearanceResolverTest {

    private AppearanceResolver resolver;
    private Landscape landscape;

    @BeforeEach
    public void setup() {

        HttpService mock = mock(HttpService.class);
        resolver = new AppearanceResolver(new ProcessLog(LoggerFactory.getLogger(AppearanceResolverTest.class)), new LocalIcons(new VendorIcons(mock)));

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
        assertEquals("https://foo.bar/icon.png", pick.getIcon());

        pick = landscape.getItems().pick("s1", "g1");
        //check icon is set
        assertThat(pick.getIcon()).contains(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64);
    }
}