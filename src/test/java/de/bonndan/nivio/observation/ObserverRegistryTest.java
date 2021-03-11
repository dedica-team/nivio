package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ObserverRegistryTest {

    private LandscapeDescriptionFactory landscapeDescriptionFactory;
    private ApplicationEventPublisher publisher;
    private LandscapeObserverFactory observerPoolFactory;
    private ObserverRegistry observerRegistry;
    private Landscape landscape;
    private ThreadPoolTaskScheduler taskScheduler;
    private IndexingDispatcher indexingDispatcher;

    @BeforeEach
    public void setup() {
        landscapeDescriptionFactory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        taskScheduler = mock(ThreadPoolTaskScheduler.class);
        observerPoolFactory = mock(LandscapeObserverFactory.class);
        indexingDispatcher = mock(IndexingDispatcher.class);
        observerRegistry = new ObserverRegistry(observerPoolFactory, taskScheduler, indexingDispatcher);
    }

    @Test
    @DisplayName("Ensure that indexed landscape is registered for observation")
    public void onProcessingFinishedEvent() {

        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);
        LandscapeDescriptionFactory landscapeDescriptionFactory = new LandscapeDescriptionFactory(new FileFetcher(new HttpService()));
        LandscapeDescription description = landscapeDescriptionFactory.fromYaml(file);

        landscape = LandscapeFactory.createForTesting(description.getIdentifier(), description.getName())
                .withContact(description.getContact())
                .withSource(source)
                .build();

        ProcessingFinishedEvent event = new ProcessingFinishedEvent(description, landscape);

        when(this.landscapeDescriptionFactory.from(landscape)).thenReturn(description);
        when(observerPoolFactory.getObserversFor(eq(landscape), eq(description))).thenReturn(new ArrayList<>());

        //when
        observerRegistry.onProcessingFinishedEvent(event);

        //then
        Set<String> observedLandscapes = observerRegistry.getObservedLandscapes();
        assertNotNull(observedLandscapes);
        assertEquals(1, observedLandscapes.size());
        assertEquals("nivio:example", observedLandscapes.iterator().next());

        verify(observerPoolFactory).getObserversFor(eq(landscape), eq(description));
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}