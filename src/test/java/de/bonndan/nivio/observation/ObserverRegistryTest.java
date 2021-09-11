package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.SeedConfiguration;
import de.bonndan.nivio.input.SeedConfigurationFactory;
import de.bonndan.nivio.input.SeedConfigurationProcessedEvent;
import de.bonndan.nivio.input.http.HttpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ObserverRegistryTest {

    private ObserverFactory observerPoolFactory;
    private ObserverRegistry observerRegistry;
    private SeedConfigurationFactory configurationFactory;

    @BeforeEach
    public void setup() {
        observerPoolFactory = mock(ObserverFactory.class);
        configurationFactory = new SeedConfigurationFactory(new FileFetcher(new HttpService()));
        observerRegistry = new ObserverRegistry(observerPoolFactory, mock(ThreadPoolTaskScheduler.class));
    }

    @Test
    @DisplayName("Ensure that indexed landscape is registered for observation")
    void onProcessingFinishedEvent() {

        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);

        SeedConfiguration configuration = configurationFactory.fromFile(file);
        SeedConfigurationProcessedEvent event = new SeedConfigurationProcessedEvent(configuration);
        when(observerPoolFactory.getObserversFor(configuration)).thenReturn(new ArrayList<>());

        //when
        observerRegistry.onProcessingFinishedEvent(event);

        //then
        Set<URL> observedLandscapes = observerRegistry.getObserved();
        assertNotNull(observedLandscapes);
        assertEquals(1, observedLandscapes.size());
        assertThat(observedLandscapes.iterator().next().toString()).contains(file.getName());

        verify(observerPoolFactory).getObserversFor(configuration);
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}