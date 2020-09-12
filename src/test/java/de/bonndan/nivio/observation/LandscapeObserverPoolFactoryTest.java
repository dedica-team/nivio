package de.bonndan.nivio.observation;

import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.InputFormatHandlerFactory;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LandscapeObserverPoolFactoryTest {

    private LandscapeImpl landscape;
    private InputFormatHandlerFactory formatFactory;
    private LandscapeObserverPoolFactory observerPoolFactory;

    @BeforeEach
    public void setup() {
        landscape = LandscapeFactory.create("test");
        formatFactory = mock(InputFormatHandlerFactory.class);
        observerPoolFactory = new LandscapeObserverPoolFactory(formatFactory);
    }

    @Test
    @DisplayName("Landscape pushed through API wont have a source url, but still source references")
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
        when(formatFactory.getInputFormatHandler(any(SourceReference.class), eq(description))).thenReturn(handler);

        //when
        LandscapeObserverPool pool = observerPoolFactory.getPoolFor(landscape, description);


        //then
        assertNotNull(pool);
        assertFalse(pool.getObservers().isEmpty());
        assertEquals(3, pool.getObservers().size());

        verify(formatFactory).getInputFormatHandler(eq(ref1),eq(description));
    }
    /*

    @Test
    public void register() {
        LandscapeDescription description = new LandscapeDescription();
        ProcessingFinishedEvent event = new ProcessingFinishedEvent(description, landscape);
        description.setIdentifier("test");
        description.setSource("https://dedica.team");
        when(landscapeDescriptionFactory.from(landscape)).thenReturn(description);
        when(urlObserverFactory.getObserver(any(URL.class))).thenReturn(mock(URLObserver.class));

        //when
        observerRegistry.onApplicationEvent(event);

        //then
        Set<String> observedLandscapes = observerRegistry.getObservedLandscapes();
        assertNotNull(observedLandscapes);
        assertEquals(1, observedLandscapes.size());
        assertEquals(landscape.getIdentifier(), observedLandscapes.iterator().next());

        verify(urlObserverFactory).getObserver(any(URL.class));
    }

    @Test
    public void willRegardRelativePaths() {

        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);
        LandscapeDescriptionFactory landscapeDescriptionFactory = new LandscapeDescriptionFactory(new FileFetcher(new HttpService()));
        LandscapeDescription description = landscapeDescriptionFactory.fromYaml(file);
        landscape.setSource(source);

        ProcessingFinishedEvent event = new ProcessingFinishedEvent(description, landscape);

        when(this.landscapeDescriptionFactory.from(landscape)).thenReturn(description);
        when(urlObserverFactory.getObserver(any(URL.class))).thenReturn(mock(URLObserver.class));

        //when
        observerRegistry.onApplicationEvent(event);

        //then
        Set<String> observedLandscapes = observerRegistry.getObservedLandscapes();
        assertNotNull(observedLandscapes);
        assertEquals(1, observedLandscapes.size());
        assertEquals("nivio:example", observedLandscapes.iterator().next());

        verify(urlObserverFactory, times(3)).getObserver(any(URL.class));
    }


     */


    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}