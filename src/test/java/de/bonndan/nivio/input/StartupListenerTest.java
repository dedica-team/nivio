package de.bonndan.nivio.input;

import de.bonndan.nivio.IndexEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StartupListenerTest {

    private LandscapeDescriptionFactory factory;
    private ApplicationEventPublisher publisher;
    private StartupListener startupListener;
    private LandscapeUrlsFactory landscapeUrlsFactory;

    @BeforeEach
    public void setup() {
        factory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        landscapeUrlsFactory = mock(LandscapeUrlsFactory.class);
        Seed seed = new Seed(); // will use Seed.NIVIO_ENV_DIRECTORY
        startupListener = new StartupListener(factory, landscapeUrlsFactory, publisher, seed);
    }

    @Test
    public void fires() throws MalformedURLException {

        //given
        when(landscapeUrlsFactory.getUrls(any(Seed.class))).thenReturn(List.of(new URL("https://dedica.team")));

        LandscapeDescription landscapeDescription = new LandscapeDescription();
        landscapeDescription.setIdentifier("foo");
        when(factory.from(any(URL.class))).thenReturn(landscapeDescription);


        //when
        startupListener.onApplicationEvent(mock(ApplicationReadyEvent.class));

        //then
        ArgumentCaptor<IndexEvent> captor = ArgumentCaptor.forClass(IndexEvent.class);
        verify(publisher).publishEvent(captor.capture());

        IndexEvent first = captor.getValue();
        assertNotNull(first);
        assertEquals("foo", first.getLandscape().getIdentifier());

    }
}