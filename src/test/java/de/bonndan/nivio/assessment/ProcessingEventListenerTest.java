package de.bonndan.nivio.assessment;

import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ProcessingEventListenerTest {

    private ProcessingEventListener listener;
    private AssessmentRepository repo;
    private ApplicationEventPublisher publisher;

    @BeforeEach
    void setUp() {
        repo = mock(AssessmentRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        listener = new ProcessingEventListener(repo, publisher);
    }

    @Test
    void onProcessingFinishedEvent() {
        //given
        Landscape landscape = LandscapeFactory.createForTesting("foo", "bar").build();
        LandscapeDescription description = new LandscapeDescription("foo");
        ProcessingFinishedEvent e = new ProcessingFinishedEvent(description, landscape, mock(ProcessingChangelog.class));

        //when
        listener.onProcessingFinishedEvent(e);

        //then
        verify(repo).save(eq(landscape.getFullyQualifiedIdentifier()), any(Assessment.class));
        verify(publisher).publishEvent(any(AssessmentChangedEvent.class));
    }
}