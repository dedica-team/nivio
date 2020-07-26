package de.bonndan.nivio.output.map;

import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PNGRenderCacheTest {

    private PNGRenderCache renderCache;
    private LocalServer localServer;
    private MapStyleSheetFactory stylesheetFactory;
    private SVGRenderer svgRenderer;

    @BeforeEach
    public void setup() {
        localServer = new LocalServer("", null);
        stylesheetFactory = mock(MapStyleSheetFactory.class);
        svgRenderer = new SVGRenderer(localServer, stylesheetFactory);
        renderCache = new PNGRenderCache(localServer, svgRenderer);
        when(stylesheetFactory.getMapStylesheet(any(), any())).thenReturn("");
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