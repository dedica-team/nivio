package de.bonndan.nivio.input;

import de.bonndan.nivio.config.SeedProperties;
import de.bonndan.nivio.input.dto.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StartupListenerTest {

    private SeedConfigurationFactory factory;
    private ApplicationEventPublisher publisher;
    private StartupListener startupListener;
    private SeedProperties seedProperties = new SeedProperties("src/test/resources/example/inout.yml","1");
    private Seed seed = new Seed(seedProperties.getSeed(), seedProperties.getDemo()); // will use Seed.NIVIO_ENV_DIRECTORY


    @BeforeEach
    public void setup() {
        factory = mock(SeedConfigurationFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        seedProperties = mock(SeedProperties.class);
        startupListener = new StartupListener(factory, publisher, seed);
    }

    @Test
    void fires() throws MalformedURLException {
        //given
        seed = new Seed("https://dedica.team", seedProperties.getDemo());
        startupListener = new StartupListener(factory, publisher, seed);

        SeedConfiguration configuration = new SeedConfiguration("foo", "bar", null);
        configuration.setSource(new Source(new URL("https://dedica.team")));
        when(factory.from(any(URL.class))).thenReturn(configuration);


        //when
        startupListener.onApplicationEvent(mock(ApplicationReadyEvent.class));

        //then
        ArgumentCaptor<SeedConfigurationChangeEvent> captor = ArgumentCaptor.forClass(SeedConfigurationChangeEvent.class);
        verify(publisher).publishEvent(captor.capture());

        SeedConfigurationChangeEvent first = captor.getValue();
        assertNotNull(first);
        assertEquals("foo", first.getConfiguration().getIdentifier());

    }
}