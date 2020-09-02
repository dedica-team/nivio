package de.bonndan.nivio.output.map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.AppearanceResolver;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.VendorIcons;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PNGRenderCacheTest {

    private PNGRenderCache renderCache;
    private LocalServer localServer;
    private MapStyleSheetFactory stylesheetFactory;
    private SVGRenderer svgRenderer;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setup() throws IOException {
        wireMockServer = new WireMockServer(options().port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        wireMockServer.stubFor(get("/icons/service.png")
                .willReturn(ok().withBody(
                        Files.readAllBytes(Paths.get("src/main/resources/static/icons/service.png")))
                ));

        localServer = new LocalServer("", null);
        stylesheetFactory = mock(MapStyleSheetFactory.class);
        svgRenderer = new SVGRenderer(stylesheetFactory);
        renderCache = new PNGRenderCache(svgRenderer);
        when(stylesheetFactory.getMapStylesheet(any(), any())).thenReturn("");
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Order(1)
    @Test
    void throwsWithoutLocalWebserverOrCache() {
        wireMockServer.stop();

        LandscapeImpl test = getLandscape("test");
        assertThrows(Exception.class, () -> renderCache.getPNG(test));
    }

    @Test
    void toPNG() {
        byte[] png = renderCache.getPNG(getLandscape("test"));
        assertNotNull(png);

        verify(stylesheetFactory, times(1)).getMapStylesheet(any(), any());
    }

    @Test
    void toPNGCached() {
        LandscapeImpl landscape = getLandscape("test");
        byte[] first = renderCache.getPNG(landscape);
        byte[] second = renderCache.getPNG(landscape);

        verify(stylesheetFactory, times(1)).getMapStylesheet(any(), any());
    }

    @Test
    void toPNGRefreshCaches() {
        byte[] first = renderCache.getPNG(getLandscape("test"));
        byte[] second = renderCache.getPNG(getLandscape("test"));

        verify(stylesheetFactory, times(2)).getMapStylesheet(any(), any());
    }

    @Test
    void cachesBasedOnIdentifier() {
        LandscapeImpl one = getLandscape("test");
        byte[] first = renderCache.getPNG(getLandscape("test"));
        LandscapeImpl two = getLandscape("test");
        two.setProcessLog(one.getLog()); //sync last update
        two.setIdentifier("second");
        byte[] second = renderCache.getPNG(two);

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

        Item item = new Item();
        item.setIdentifier("foo");
        item.setGroup("bar");
        landscape.getItems().add(item);

        Group bar = new Group("bar");
        bar.getItems().add(item);
        landscape.getGroups().put("bar", bar);

        ProcessLog test = new ProcessLog(LoggerFactory.getLogger("test"));
        test.info("foo");
        landscape.setProcessLog(test);

        new AppearanceResolver(landscape.getLog(), new LocalServer("", new VendorIcons())).process(null, landscape);
        return landscape;
    }
}