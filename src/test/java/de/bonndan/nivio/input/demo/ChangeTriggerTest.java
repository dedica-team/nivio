package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChangeTriggerTest {

    private LandscapeDescriptionFactory factory;
    private ApplicationEventPublisher publisher;
    private ChangeTrigger trigger;

    @BeforeEach
    void setUp() {
        factory = mock(LandscapeDescriptionFactory.class);
        publisher = mock(ApplicationEventPublisher.class);
        trigger = new ChangeTrigger(factory, publisher);
    }

    @Test
    void doesNotTriggerWithoutDemo() {
        trigger.trigger();
        verify(publisher, never()).publishEvent(any());
    }
}