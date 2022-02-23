package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ProcessLog;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MapStyleSheetFactoryTest {

    private FileFetcher fileFetcher;
    private MapStyleSheetFactory factory;
    private Landscape landscape;
    private ProcessLog processLog;

    @BeforeEach
    void getMapStylesheet() {
        fileFetcher = mock(FileFetcher.class);
        factory = new MapStyleSheetFactory(fileFetcher);
        processLog = mock(ProcessLog.class);
        landscape = LandscapeFactory.createForTesting("test", "testLandscape").build();
        landscape.setLog(new ProcessLog(mock(Logger.class), "test"));
        landscape.getConfig().getBranding().setMapStylesheet("http://acme.com/test.css");
    }

    @Test
     void returnsFile() {
        when(fileFetcher.get(any(URL.class))).thenReturn("foo {}");

        String s = factory.getMapStylesheet(landscape.getConfig(), landscape.getLog());
        assertEquals("foo {}", s);
    }

    @Test
     void returnsEmptyString() {
        when(fileFetcher.get(any(URL.class))).thenThrow(new ReadingException("whatever", new RuntimeException("foo")));

        String s = factory.getMapStylesheet(landscape.getConfig(), landscape.getLog());
        assertEquals("", s);

        verify(processLog).warn(anyString());
    }
}