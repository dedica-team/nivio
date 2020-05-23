package de.bonndan.nivio.input;

import de.bonndan.nivio.IndexEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StartupListenerTest {

    private LandscapeDescriptionFactory factory;
    private ApplicationEventPublisher publisher;
    private StartupListener startupListener;

    @BeforeEach
    public void setup() {
        factory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        Seed seed = new Seed(); // will use Seed.NIVIO_ENV_DIRECTORY
        startupListener = new StartupListener(factory, publisher, seed);
    }

    @Test
    public void fires() {

        //given
        LandscapeDescription landscapeDescription = new LandscapeDescription();
        landscapeDescription.setIdentifier("foo");
        List<LandscapeDescription> descriptionList = List.of(landscapeDescription);
        when(factory.getDescriptions(any(Seed.class))).thenReturn(descriptionList);

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