package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
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

class ObserverFactoryTest {

    private InputFormatHandlerFactory formatFactory;
    private ObserverFactory observerPoolFactory;
    private FileFetcher fileFetcher;
    private SeedConfigurationFactory seedConfigurationFactory;

    @BeforeEach
    public void setup() {
        formatFactory = mock(InputFormatHandlerFactory.class);
        fileFetcher = mock(FileFetcher.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        observerPoolFactory = new ObserverFactory(formatFactory, fileFetcher, publisher);
        seedConfigurationFactory = new SeedConfigurationFactory(new FileFetcher(new HttpService()));
    }

    @Test
    @DisplayName("creates a list of observers with correct base url")
    void getObservers() throws MalformedURLException {
        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);
        SeedConfiguration description = seedConfigurationFactory.fromFile(file);

        SourceReference ref1 = new SourceReference(new URL("https://test.com"));
        description.getSourceReferences().add(ref1);

        InputFormatHandler handler = mock(InputFormatHandler.class);
        InputFormatObserver mockObserver = mock(InputFormatObserver.class);
        when(handler.getObserver(any(InputFormatObserver.class), any(ApplicationEventPublisher.class), any(SourceReference.class))).thenReturn(mockObserver);
        when(formatFactory.getInputFormatHandler(any(SourceReference.class))).thenReturn(handler);

        when(fileFetcher.get(any(URL.class))).thenReturn("");

        //when
        List<InputFormatObserver> observers = observerPoolFactory.getObserversFor(description);


        //then
        assertNotNull(observers);
        assertFalse(observers.isEmpty());
        assertEquals(4, observers.size());


        verify(formatFactory).getInputFormatHandler(eq(ref1));
        verify(handler).getObserver(any(InputFormatObserver.class), any(ApplicationEventPublisher.class), eq(ref1));
    }

    @Test
    @DisplayName("Landscape pushed through API wont have a source url, but still source references")
    void withoutSourceUrl() throws MalformedURLException {
        SeedConfiguration configuration = seedConfigurationFactory.fromFile(new File(getRootPath() + "/src/test/resources/example/example_env.yml"));
        configuration.setSource(null);

        configuration.getSourceReferences().clear();
        SourceReference ref1 = new SourceReference(new URL("https://dedica.team"));
        configuration.getSourceReferences().add(ref1);

        InputFormatHandler handler = mock(InputFormatHandler.class);
        when(handler.getObserver(any(InputFormatObserver.class), any(ApplicationEventPublisher.class), any(SourceReference.class))).thenReturn(mock(InputFormatObserver.class));
        when(formatFactory.getInputFormatHandler(any(SourceReference.class))).thenReturn(handler);

        //when
        List<InputFormatObserver> observers = observerPoolFactory.getObserversFor(configuration);


        //then
        assertNotNull(observers);
        assertFalse(observers.isEmpty());
        assertEquals(1, observers.size());

        verify(formatFactory).getInputFormatHandler(ref1);
        verify(handler).getObserver(any(InputFormatObserver.class), any(ApplicationEventPublisher.class), eq(ref1));
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}