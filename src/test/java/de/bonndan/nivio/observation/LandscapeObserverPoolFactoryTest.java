package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.InputFormatHandlerFactory;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.util.URLHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LandscapeObserverPoolFactoryTest {

    private Landscape landscape;
    private InputFormatHandlerFactory formatFactory;
    private LandscapeObserverPoolFactory observerPoolFactory;
    private FileFetcher fileFetcher;

    @BeforeEach
    public void setup() {
        landscape = LandscapeFactory.create("test");
        formatFactory = mock(InputFormatHandlerFactory.class);
        fileFetcher = mock(FileFetcher.class);
        observerPoolFactory = new LandscapeObserverPoolFactory(formatFactory, fileFetcher);
    }

    @Test
    @DisplayName("creates a pool with correct base rul")
    public void getPool() {
        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);
        LandscapeDescriptionFactory landscapeDescriptionFactory = new LandscapeDescriptionFactory(new FileFetcher(new HttpService()));
        LandscapeDescription description = landscapeDescriptionFactory.fromYaml(file);
        landscape.setSource(source);

        SourceReference ref1 = new SourceReference();
        ref1.setUrl("https://dedica.team");
        description.getSourceReferences().add(ref1);

        InputFormatHandler handler = mock(InputFormatHandler.class);
        when(formatFactory.getInputFormatHandler(any(SourceReference.class))).thenReturn(handler);

        when(fileFetcher.get(any(URL.class))).thenReturn("");

        //when
        LandscapeObserverPool pool = observerPoolFactory.getPoolFor(landscape, description);


        //then
        assertNotNull(pool);
        assertFalse(pool.getObservers().isEmpty());
        assertEquals(4, pool.getObservers().size());

        verify(formatFactory).getInputFormatHandler(eq(ref1));
        verify(handler).getObserver(eq(ref1), eq(URLHelper.getURL(getRootPath() + "/src/test/resources/example/").get()));
    }

    @Test
    @DisplayName("Landscape pushed through API wont have a source url, but still source references")
    public void withoutSourceUrl() {
        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);
        LandscapeDescriptionFactory landscapeDescriptionFactory = new LandscapeDescriptionFactory(new FileFetcher(new HttpService()));
        LandscapeDescription description = landscapeDescriptionFactory.fromYaml(file);
        description.setSource(null);
        landscape.setSource(null);

        SourceReference ref1 = new SourceReference();
        ref1.setUrl("https://dedica.team");
        description.getSourceReferences().add(ref1);

        InputFormatHandler handler = mock(InputFormatHandler.class);
        when(formatFactory.getInputFormatHandler(any(SourceReference.class))).thenReturn(handler);

        //when
        LandscapeObserverPool pool = observerPoolFactory.getPoolFor(landscape, description);


        //then
        assertNotNull(pool);
        assertFalse(pool.getObservers().isEmpty());
        assertEquals(3, pool.getObservers().size());

        verify(formatFactory).getInputFormatHandler(eq(ref1));
        verify(handler).getObserver(eq(ref1), eq(null));
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}