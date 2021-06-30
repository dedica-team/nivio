package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StartupListenerTest {

    private LandscapeDescriptionFactory factory;
    private ApplicationEventPublisher publisher;
    private StartupListener startupListener;
    private Seed seed = new Seed(Optional.empty()); // will use Seed.NIVIO_ENV_DIRECTORY

    @BeforeEach
    public void setup() {
        factory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        startupListener = new StartupListener(factory, publisher, seed);
    }

    @Test
    public void fires() throws MalformedURLException {

        //given
        seed = new Seed(Optional.of("https://dedica.team"));
        startupListener = new StartupListener(factory, publisher, seed);

        LandscapeDescription landscapeDescription = new LandscapeDescription("foo", "bar", null);
        landscapeDescription.setSource(new LandscapeSource(new URL("https://dedica.team")));
        when(factory.from(any(URL.class))).thenReturn(landscapeDescription);


        //when
        startupListener.onApplicationEvent(mock(ApplicationReadyEvent.class));

        //then
        ArgumentCaptor<IndexEvent> captor = ArgumentCaptor.forClass(IndexEvent.class);
        verify(publisher).publishEvent(captor.capture());

        IndexEvent first = captor.getValue();
        assertNotNull(first);
        assertEquals("foo", first.getLandscapeDescription().getIdentifier());

    }
}