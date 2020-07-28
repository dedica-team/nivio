package de.bonndan.nivio.output.map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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
        svgRenderer = new SVGRenderer(localServer, stylesheetFactory);
        renderCache = new PNGRenderCache(localServer, svgRenderer);
        when(stylesheetFactory.getMapStylesheet(any(), any())).thenReturn("");
    }

    @AfterEach
    void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    void toPNG() {
        byte[] png = renderCache.getPNG(getLandscape());
        assertNotNull(png);

        verify(stylesheetFactory, times(1)).getMapStylesheet(any(), any());
    }

    @Test
    void toPNGCached() {
        LandscapeImpl landscape = getLandscape();
        byte[] first = renderCache.getPNG(landscape);
        byte[] second = renderCache.getPNG(landscape);

        verify(stylesheetFactory, times(1)).getMapStylesheet(any(), any());
    }

    @Test
    void toPNGRefreshCaches() {
        byte[] first = renderCache.getPNG(getLandscape());
        byte[] second = renderCache.getPNG(getLandscape());

        verify(stylesheetFactory, times(2)).getMapStylesheet(any(), any());
    }

    @Test
    void cachesBasedOnIdentifier() {
        LandscapeImpl one = getLandscape();
        byte[] first = renderCache.getPNG(getLandscape());
        LandscapeImpl two = getLandscape();
        two.setProcessLog(one.getLog()); //sync last update
        two.setIdentifier("second");
        byte[] second = renderCache.getPNG(two);

        verify(stylesheetFactory, times(2)).getMapStylesheet(any(), any());
    }

    @Test
    void toSVG() {
        String svg = renderCache.getSVG(getLandscape());
        assertNotNull(svg);
        assertTrue(svg.contains("svg"));
    }

    @Test
    void onProcessingFinishedEvent() {
        renderCache.onApplicationEvent(new ProcessingFinishedEvent(new LandscapeDescription(), getLandscape()));

        verify(stylesheetFactory, times(1)).getMapStylesheet(any(), any());
    }

    private LandscapeImpl getLandscape() {
        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("test");
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

        return landscape;
    }
}