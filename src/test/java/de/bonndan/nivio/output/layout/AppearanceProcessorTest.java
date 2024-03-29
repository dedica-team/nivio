package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.icons.DataUrlHelper;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.model.ItemFactory.getTestItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppearanceProcessorTest {

    private AppearanceProcessor resolver;
    private Landscape landscape;
    private IconService iconService;
    private LocalIcons localIcons;
    private Group g1;
    private ArrayList<Item> items;

    @BeforeEach
    public void setup() {

        iconService = mock(IconService.class);
        localIcons = mock(LocalIcons.class);
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
    void item_icon_setIconAndFillAppearance() {
        Item s1 = landscape.getItems().pick("s1", "g1");
        s1.setLabel(Label.icon, "https://dedica.team/images/logo_orange_weiss.png");
        when(iconService.getIconUrl(s1)).thenReturn(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");

        // when
        resolver.process(landscape);

        // then
        assertThat(s1.getLabel(Label._icondata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }

    @Test
    void item_fill_setIconAndFillAppearance() throws MalformedURLException {
        Item s1 = landscape.getItems().pick("s1", "g1");
        s1.setLabel(Label.fill, "http://dedica.team/images/portrait.jpeg");
        when(iconService.getExternalUrl(new URL(s1.getLabel(Label.fill)))).thenReturn(java.util.Optional.of(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo"));

        // when
        resolver.process(landscape);

        // then
        assertThat(s1.getLabel(Label._filldata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }

    @Test
    void landscape_icon_setIconAndFillAppearance() throws MalformedURLException {

        // given
        landscape.setLabel(Label.icon, "https://dedica.team/images/logo_orange_weiss.png");
        when(iconService.getExternalUrl(new URL(landscape.getLabel(Label.icon)))).thenReturn(java.util.Optional.of(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo"));

        // when
        resolver.process(landscape);

        // then
        assertThat(landscape.getLabel(Label._icondata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }

    @Test
    void landscape_fill_setIconAndFillAppearance() throws MalformedURLException {

        // given
        landscape.setLabel(Label.fill, "http://dedica.team/images/portrait.jpeg");
        when(iconService.getExternalUrl(new URL(landscape.getLabel(Label.fill)))).thenReturn(java.util.Optional.of(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo"));

        // when
        resolver.process(landscape);

        // then
        assertThat(landscape.getLabel(Label._filldata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }

    @Test
    void group_icon_setIconAndFillAppearance() throws MalformedURLException {

        // given
        g1.setLabel(Label.icon, "https://dedica.team/images/logo_orange_weiss.png");
        when(iconService.getExternalUrl(new URL(g1.getLabel(Label.icon)))).thenReturn(java.util.Optional.of(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo"));

        // when
        resolver.process(landscape);

        // then
        assertThat(g1.getLabel(Label._icondata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }

    @Test
    void group_fill_setIconAndFillAppearance() throws MalformedURLException {

        // given
        g1.setLabel(Label.fill, "http://dedica.team/images/portrait.jpeg");
        when(iconService.getExternalUrl(new URL(g1.getLabel(Label.fill)))).thenReturn(java.util.Optional.of(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo"));

        // when
        resolver.process(landscape);

        // then
        assertThat(g1.getLabel(Label._filldata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }

    @Test
    void group_setDefaultIcon_setIconAndFillAppearance() {

        // given
        // default group icon
        when(localIcons.getDefaultGroupIcon()).thenReturn(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "PD94bWwg");

        // when
        resolver.process(landscape);

        // then
        assertThat(g1.getIcon()).startsWith(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "PD94bWwg");
    }

}