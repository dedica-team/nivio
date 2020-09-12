package de.bonndan.nivio.observation;

import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

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
    private LandscapeObserverPoolFactory observerPoolFactory;
    private ObserverRegistry observerRegistry;
    private LandscapeImpl landscape;

    @BeforeEach
    public void setup() {
        landscapeDescriptionFactory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        observerPoolFactory = mock(LandscapeObserverPoolFactory.class);
        observerRegistry = new ObserverRegistry(observerPoolFactory, landscapeDescriptionFactory, publisher);
    }

    @Test
    public void onApplicationEvent() {

        String source = getRootPath() + "/src/test/resources/example/example_env.yml";
        File file = new File(source);
        LandscapeDescriptionFactory landscapeDescriptionFactory = new LandscapeDescriptionFactory(new FileFetcher(new HttpService()));
        LandscapeDescription description = landscapeDescriptionFactory.fromYaml(file);

        landscape = LandscapeFactory.create(description.getIdentifier());
        landscape.setSource(source);

        ProcessingFinishedEvent event = new ProcessingFinishedEvent(description, landscape);

        when(this.landscapeDescriptionFactory.from(landscape)).thenReturn(description);
        when(observerPoolFactory.getPoolFor(eq(landscape), eq(description))).thenReturn(new LandscapeObserverPool(landscape, new ArrayList<>()));

        //when
        observerRegistry.onApplicationEvent(event);

        //then
        Set<String> observedLandscapes = observerRegistry.getObservedLandscapes();
        assertNotNull(observedLandscapes);
        assertEquals(1, observedLandscapes.size());
        assertEquals("nivio:example", observedLandscapes.iterator().next());

        verify(observerPoolFactory).getPoolFor(eq(landscape), eq(description));
    }

    private String getRootPath() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}