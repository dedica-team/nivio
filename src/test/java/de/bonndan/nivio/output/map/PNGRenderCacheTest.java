package de.bonndan.nivio.output.map;

import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import org.apache.catalina.startup.UserConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PNGRenderCacheTest {

    private PNGRenderCache renderCache;
    private MapFactory mapFactory;
    private MapStyleSheetFactory stylesheetFactory;

    @BeforeEach
    public void setup() {
        mapFactory = mock(MapFactory.class);
        stylesheetFactory = mock(MapStyleSheetFactory.class);
        renderCache = new PNGRenderCache(mapFactory, stylesheetFactory);
        when(stylesheetFactory.getMapStylesheet(any(), any())).thenReturn("");

        doAnswer(invocationOnMock -> {
            LandscapeImpl argument = invocationOnMock.getArgument(0);
            argument.getItems().all().forEach(item -> {
                item.setX(50L);
                item.setY(50L);
            });
            return null;
        }).when(mapFactory).applyArtifactValues(any(LandscapeImpl.class), any());
    }

    @Test
    void toPNG() {
        byte[] png = renderCache.getPNG(getLandscape());
        assertNotNull(png);

        verify(mapFactory, times(1)).applyArtifactValues(any(), any());
    }

    @Test
    void toPNGCached() {
        LandscapeImpl landscape = getLandscape();
        byte[] first = renderCache.getPNG(landscape);
        byte[] second = renderCache.getPNG(landscape);

        verify(mapFactory, times(1)).applyArtifactValues(any(), any());
    }

    @Test
    void toPNGRefreshCaches() {
        byte[] first = renderCache.getPNG(getLandscape());
        byte[] second = renderCache.getPNG(getLandscape());

        verify(mapFactory, times(2)).applyArtifactValues(any(), any());
    }

    @Test
    void cachesBasedOnIdentifier() {
        LandscapeImpl one = getLandscape();
        byte[] first = renderCache.getPNG(getLandscape());
        LandscapeImpl two = getLandscape();
        two.setProcessLog(one.getLog()); //sync last update
        two.setIdentifier("second");
        byte[] second = renderCache.getPNG(two);

        verify(mapFactory, times(2)).applyArtifactValues(any(), any());
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

        verify(mapFactory, times(1)).applyArtifactValues(any(), any());
    }

    private LandscapeImpl getLandscape() {
        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier("test");
        Item item = new Item();
        item.setIdentifier("foo");
        landscape.getItems().add(item);

        ProcessLog test = new ProcessLog(LoggerFactory.getLogger("test"));
        test.info("foo");
        landscape.setProcessLog(test);

        return landscape;
    }
}