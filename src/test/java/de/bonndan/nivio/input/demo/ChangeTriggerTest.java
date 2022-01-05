package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.input.SeedConfigurationFactory;
import de.bonndan.nivio.model.LandscapeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ChangeTriggerTest {

    private SeedConfigurationFactory seedConfigurationFactory;
    private IndexingDispatcher indexingDispatcher;
    private ChangeTrigger trigger;

    @BeforeEach
    void setUp() {
        seedConfigurationFactory = mock(SeedConfigurationFactory.class);
        indexingDispatcher = mock(IndexingDispatcher.class);
        trigger = new ChangeTrigger(new LandscapeRepository(), seedConfigurationFactory, indexingDispatcher);
    }

    @Test
    void doesNotTriggerWithoutDemo() {
        trigger.trigger();
        verify(indexingDispatcher, never()).handle(any());
    }
}