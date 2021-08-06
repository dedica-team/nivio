package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.InputFormatHandlerFactory;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeSource;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LandscapeObserverFactoryTest {

    private Landscape landscape;
    private InputFormatHandlerFactory formatFactory;
    private LandscapeObserverFactory observerPoolFactory;
    private FileFetcher fileFetcher;

    @BeforeEach
    public void setup() {
        landscape = LandscapeFactory.createForTesting("test", "testLandscape").build();
        formatFactory = mock(InputFormatHandlerFactory.class);
        fileFetcher = mock(FileFetcher.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        observerPoolFactory = new LandscapeObserverFactory(formatFactory, fileFetcher, publisher);
    }

    @Test
    @DisplayName("creates a list of observers with correct base url")
    public void getObservers() throws MalformedURLException {
        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);
        LandscapeDescriptionFactory landscapeDescriptionFactory = new LandscapeDescriptionFactory(new FileFetcher(new HttpService()));
        LandscapeDescription description = landscapeDescriptionFactory.fromYaml(file);
        landscape = LandscapeFactory.createForTesting("test", "testLandscape")
                .withSource(new LandscapeSource(file.toURI().toURL()))
                .build();

        SourceReference ref1 = new SourceReference();
        ref1.setUrl("https://dedica.team");
        description.getSourceReferences().add(ref1);

        InputFormatHandler handler = mock(InputFormatHandler.class);
        InputFormatObserver mockObserver = mock(InputFormatObserver.class);
        when(handler.getObserver(any(ApplicationEventPublisher.class), any(Landscape.class), any(SourceReference.class))).thenReturn(mockObserver);
        when(formatFactory.getInputFormatHandler(any(SourceReference.class))).thenReturn(handler);

        when(fileFetcher.get(any(URL.class))).thenReturn("");

        //when
        List<InputFormatObserver> observers = observerPoolFactory.getObserversFor(landscape, description);


        //then
        assertNotNull(observers);
        assertFalse(observers.isEmpty());
        assertEquals(4, observers.size());


        verify(formatFactory).getInputFormatHandler(eq(ref1));
        verify(handler).getObserver(any(ApplicationEventPublisher.class), any(Landscape.class), eq(ref1));
    }

    @Test
    @DisplayName("Landscape pushed through API wont have a source url, but still source references")
    public void withoutSourceUrl() {
        LandscapeDescriptionFactory landscapeDescriptionFactory = new LandscapeDescriptionFactory(new FileFetcher(new HttpService()));
        LandscapeDescription description = landscapeDescriptionFactory.fromYaml(new File(getRootPath() + "/src/test/resources/example/example_env.yml"));
        description.setSource(null);

        description.getSourceReferences().clear();
        SourceReference ref1 = new SourceReference();
        ref1.setUrl("https://dedica.team");
        description.getSourceReferences().add(ref1);

        InputFormatHandler handler = mock(InputFormatHandler.class);
        when(handler.getObserver(any(ApplicationEventPublisher.class), any(Landscape.class), any(SourceReference.class))).thenReturn(mock(InputFormatObserver.class));
        when(formatFactory.getInputFormatHandler(any(SourceReference.class))).thenReturn(handler);

        //when
        List<InputFormatObserver> observers = observerPoolFactory.getObserversFor(landscape, description);


        //then
        assertNotNull(observers);
        assertFalse(observers.isEmpty());
        assertEquals(1, observers.size());

        verify(formatFactory).getInputFormatHandler(eq(ref1));
        verify(handler).getObserver(any(ApplicationEventPublisher.class), any(Landscape.class), eq(ref1));
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}