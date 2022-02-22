package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.icons.DataUrlHelper;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppearanceProcessorTest {

    private AppearanceProcessor resolver;
    private Landscape landscape;
    private IconService iconService;
    private LocalIcons localIcons;
    private Group g1;

    @BeforeEach
    public void setup() {

        iconService = mock(IconService.class);
        localIcons = mock(LocalIcons.class);
        resolver = new AppearanceProcessor(iconService);

        var graph = new GraphTestSupport();
        landscape = graph.landscape;

        g1 = graph.getTestGroup("g1");

        Item s1 = graph.getTestItemBuilder("g1", "s1").withType("loadbalancer").build();
        landscape.getIndexWriteAccess().addOrReplaceChild(s1);

        Item s2 = graph.getTestItem("g1", "s2");
        s2.setLabel(Label.icon, "https://foo.bar/icon.png");
    }

    @Test
    void item_icon_setIconAndFillAppearance() {
        Item s1 = landscape.getIndexReadAccess().matchOneByIdentifiers("s1", "g1", Item.class).orElseThrow();
        s1.setLabel(Label.icon, "https://dedica.team/images/logo_orange_weiss.png");
        when(iconService.getIconUrl(s1)).thenReturn(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");

        // when
        resolver.process(landscape);

        // then
        assertThat(s1.getLabel(Label._icondata)).isEqualTo(DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64 + "foo");
    }

    @Test
    void item_fill_setIconAndFillAppearance() throws MalformedURLException {
        Item s1 = landscape.getIndexReadAccess().matchOneByIdentifiers("s1", "g1", Item.class).orElseThrow();
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