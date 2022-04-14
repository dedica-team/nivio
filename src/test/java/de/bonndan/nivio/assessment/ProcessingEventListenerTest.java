package de.bonndan.nivio.assessment;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProcessingEventListenerTest {

    private ProcessingEventListener listener;
    private AssessmentRepository repo;
    private AssessmentFactory factory;
    private ApplicationEventPublisher publisher;
    private GraphTestSupport graph;

    @BeforeEach
    void setUp() {
        repo = mock(AssessmentRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        factory = mock(AssessmentFactory.class);
        listener = new ProcessingEventListener(repo, factory, publisher);

        graph = new GraphTestSupport();
    }

    @Test
    void onProcessingFinishedEvent() {
        //given
        LandscapeDescription description = new LandscapeDescription(graph.landscape.getIdentifier());
        URI uri = graph.itemAA.getFullyQualifiedIdentifier();
        when(factory.createAssessment(any(Landscape.class))).thenReturn(new Assessment(Map.of(uri, List.of(StatusValue.summary(uri, new ArrayList<>())))));
        ProcessingFinishedEvent e = new ProcessingFinishedEvent(description, graph.landscape, mock(ProcessingChangelog.class));

        //when
        listener.onProcessingFinishedEvent(e);

        //then
        verify(repo).save(eq(graph.landscape.getFullyQualifiedIdentifier()), any(Assessment.class));
        verify(publisher).publishEvent(any(AssessmentChangedEvent.class));
    }

    @Test
    @DisplayName("Does not propagate event if assessment has not changed")
    void noEvent() {
        //given
        LandscapeDescription description = new LandscapeDescription(graph.landscape.getIdentifier());
        URI uri = graph.itemAA.getFullyQualifiedIdentifier();
        ProcessingFinishedEvent e = new ProcessingFinishedEvent(description, graph.landscape, mock(ProcessingChangelog.class));
        Assessment assessment = Assessment.empty();
        when(factory.createAssessment(any(Landscape.class))).thenReturn(assessment);
        when(repo.getAssessment(any())).thenReturn(Optional.of(assessment));

        //when
        listener.onProcessingFinishedEvent(e);

        //then
        verify(repo).save(eq(graph.landscape.getFullyQualifiedIdentifier()), any(Assessment.class));
        verify(publisher, never()).publishEvent(any(AssessmentChangedEvent.class));
    }
}