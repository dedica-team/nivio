package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.icons.DataUrlHelper;
import de.bonndan.nivio.output.icons.IconService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppearanceProcessorTest {

    private AppearanceProcessor resolver;
    private Landscape landscape;
    private IconService iconService;
    private Group g1;
    private ArrayList<Item> items;

    @BeforeEach
    public void setup() {

        iconService = mock(IconService.class);
        landscape = LandscapeFactory.createForTesting("l1", "l1Landscape").build();
        resolver = new AppearanceProcessor(iconService);


        g1 = new Group("g1", "landscapeIdentifier");
        landscape.addGroup(g1);
        items = new ArrayList<>();

        Item s1 = getTestItemBuilder("g1", "s1").withLandscape(landscape).withType("loadbalancer").build();

        items.add(s1);
        g1.addOrReplaceItem(s1);

        Item s2 = getTestItem("g1", "s2", landscape);

        s2.setLabel(Label.icon, "https://foo.bar/icon.png");
        items.add(s2);
        g1.addOrReplaceItem(s2);

        landscape.setItems(new HashSet<>(items));
    }

    @Test
    void setsItemIcons() {

        Item pick = landscape.getItems().pick("s1", "g1");
        when(iconService.getIconUrl(eq(pick))).thenReturn(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
        //when
        resolver.process(landscape);

        //then

        //check icon is set
        assertThat(pick.getLabel(Label._icondata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }

    @Test
    void setLandscapeItems() {

        // given

//        landscape.setLabel("icon", "https://dedica.team/images/logo.png");
        landscape.setLabel("icon", "muh");

        //when
        resolver.process(landscape);

        //then
        System.out.println(landscape);
        //check icon is set
        assertThat(landscape.getLabel(Label._icondata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }
}