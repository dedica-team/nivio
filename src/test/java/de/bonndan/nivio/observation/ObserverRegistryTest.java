package de.bonndan.nivio.observation;

import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.LandscapeUrlsFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ObserverRegistryTest {

    private ObserverRegistry observerRegistry;
    private LandscapeDescriptionFactory landscapeDescriptionFactory;
    private LandscapeUrlsFactory landscapeUrlsFactory;
    private ApplicationEventPublisher publisher;
    private URLObserverFactory urlObserverFactory;
    private LandscapeImpl landscape;

    @BeforeEach
    public void setup() {
        landscapeDescriptionFactory = mock(LandscapeDescriptionFactory.class);
        landscapeUrlsFactory = mock(LandscapeUrlsFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        urlObserverFactory = mock(URLObserverFactory.class);
        observerRegistry = new ObserverRegistry(urlObserverFactory, landscapeDescriptionFactory, landscapeUrlsFactory, publisher);

        landscape = new LandscapeImpl();
        landscape.setIdentifier("test");
    }

    @Test
    public void register() {
        LandscapeDescription description = new LandscapeDescription();
        ProcessingFinishedEvent event = new ProcessingFinishedEvent(description, landscape);
        description.setIdentifier("test");
        description.setSource("https://dedica.team");
        when(landscapeDescriptionFactory.from(landscape)).thenReturn(description);
        when(landscapeUrlsFactory.getLandscapeSourceLocations(any(LandscapeDescription.class), any(URL.class)))
                .thenAnswer(invocationOnMock -> {
                    ArrayList<URL> urls = new ArrayList<>();
                    urls.add(new URL("https://dedica.team"));
                    return urls;
                });
        when(urlObserverFactory.getObserver(any(URL.class))).thenReturn(mock(URLObserver.class));

        //when
        observerRegistry.onApplicationEvent(event);

        //then
        Set<Landscape> observedLandscapes = observerRegistry.getObservedLandscapes();
        assertNotNull(observedLandscapes);
        assertEquals(1, observedLandscapes.size());
        assertEquals(landscape.getIdentifier(), observedLandscapes.iterator().next().getIdentifier());

        verify(urlObserverFactory).getObserver(any(URL.class));
    }

    @Test
    @DisplayName("Landscape pushed through API wont have a source url, but still source references")
    public void willRegisterWithoutSourceURL() {
        LandscapeDescription description = new LandscapeDescription();
        ProcessingFinishedEvent event = new ProcessingFinishedEvent(description, landscape);
        description.setIdentifier("test");
        description.setSource("pushed by api");
        when(landscapeDescriptionFactory.from(landscape)).thenReturn(description);
        when(landscapeUrlsFactory.getLandscapeSourceLocations(any(LandscapeDescription.class), eq(null)))
                .thenAnswer(invocationOnMock -> {
                    ArrayList<URL> urls = new ArrayList<>();
                    urls.add(new URL("https://dedica.team"));
                    return urls;
                });

        //when
        observerRegistry.onApplicationEvent(event);

        //then
        Set<Landscape> observedLandscapes = observerRegistry.getObservedLandscapes();
        assertNotNull(observedLandscapes);
        assertEquals(1, observedLandscapes.size());
        assertEquals(landscape.getIdentifier(), observedLandscapes.iterator().next().getIdentifier());

        verify(urlObserverFactory).getObserver(any(URL.class));
    }
}