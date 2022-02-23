package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.AssessmentChangedEvent;
import de.bonndan.nivio.assessment.AssessmentRepository;
import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

class SearchIndexingEventListenerTest {

    private Landscape landscape;
    private AssessmentRepository assessmentRepository;
    private SearchIndexingEventListener listener;

    @BeforeEach
    void setUp() {
        landscape = mock(Landscape.class);
        assessmentRepository = mock(AssessmentRepository.class);
        when(landscape.getFullyQualifiedIdentifier()).thenReturn(FullyQualifiedIdentifier.build(ComponentClass.landscape, "foo"));
        listener = new SearchIndexingEventListener(assessmentRepository);
    }

    @Test
    void onAssessmentChangedEvent() {
        //given
        AssessmentChangedEvent e = new AssessmentChangedEvent(landscape, new ProcessingChangelog());
        IndexReadAccess<GraphComponent> readAccess = mock(IndexReadAccess.class);
        when(landscape.getIndexReadAccess()).thenReturn(readAccess);
        when(landscape.getKpis()).thenReturn(Map.of());
        when(assessmentRepository.getAssessment(any())).thenReturn(java.util.Optional.of(Assessment.empty()));

        //when
        listener.onProcessingFinishedEvent(e);

        //then
        verify(landscape).getIndexReadAccess();
        verify(readAccess).indexForSearch(any(Assessment.class));
    }
}