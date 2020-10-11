package de.bonndan.nivio.output.map;

import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.AppearanceResolver;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.LocalIcons;
import de.bonndan.nivio.output.icons.VendorIcons;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RenderCacheTest {

    private RenderCache renderCache;
    private MapStyleSheetFactory stylesheetFactory;
    private SVGRenderer svgRenderer;

    @BeforeEach
    public void setup() {

        stylesheetFactory = mock(MapStyleSheetFactory.class);
        svgRenderer = new SVGRenderer(stylesheetFactory);
        renderCache = new RenderCache(svgRenderer);
        when(stylesheetFactory.getMapStylesheet(any(), any())).thenReturn("");
    }

    @Test
    void toPNGCached() {
        LandscapeImpl landscape = getLandscape("test");
        String first = renderCache.getSVG(landscape);
        String second = renderCache.getSVG(landscape);

        verify(stylesheetFactory, times(1)).getMapStylesheet(any(), any());
    }

    @Test
    void cachesBasedOnIdentifier() {
        LandscapeImpl one = getLandscape("test");
        String first = renderCache.getSVG(getLandscape("test"));
        LandscapeImpl two = getLandscape("test");
        two.setProcessLog(one.getLog()); //sync last update
        two.setIdentifier("second");
        String second = renderCache.getSVG(two);

        verify(stylesheetFactory, times(2)).getMapStylesheet(any(), any());
    }

    @Test
    void toSVG() {
        String svg = renderCache.getSVG(getLandscape("test"));
        assertNotNull(svg);
        assertTrue(svg.contains("svg"));
    }

    @Test
    void onProcessingFinishedEvent() {
        renderCache.onApplicationEvent(new ProcessingFinishedEvent(new LandscapeDescription(), getLandscape("test")));

        verify(stylesheetFactory, times(1)).getMapStylesheet(any(), any());
    }

    private LandscapeImpl getLandscape(String identifier) {
        LandscapeImpl landscape = LandscapeFactory.create(identifier);

        Item item = new Item("bar", "foo");
        landscape.setItems(Set.of(item));
        landscape.setItems(Collections.singleton(item));

        Group bar = new Group("bar");
        bar.addItem(item);
        landscape.getGroups().put("bar", bar);

        ProcessLog test = new ProcessLog(LoggerFactory.getLogger("test"));
        test.info("foo");
        landscape.setProcessLog(test);

        HttpService httpService = mock(HttpService.class);
        new AppearanceResolver(landscape.getLog(), new LocalIcons(new VendorIcons(httpService))).process(null, landscape);
        return landscape;
    }
}