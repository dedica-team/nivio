package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.AssessmentFactory;
import de.bonndan.nivio.assessment.AssessmentRepository;
import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.Mockito.*;

class SearchIndexingEventListenerTest {

    private AssessmentRepository assessmentRepository;
    private Landscape landscape;
    private SearchIndexingEventListener listener;

    @BeforeEach
    void setUp() {
        assessmentRepository = mock(AssessmentRepository.class);
        landscape = mock(Landscape.class);
        listener = new SearchIndexingEventListener(assessmentRepository);
    }

    @Test
    void onProcessingFinishedEvent() {
        //given
        var assessment = AssessmentFactory.createAssessment(landscape);
        when(assessmentRepository.getAssessment(Mockito.any())).thenReturn(assessment);
        ProcessingFinishedEvent e = new ProcessingFinishedEvent(new LandscapeDescription("foo"), landscape, new ProcessingChangelog());
        SearchIndex searchIndex = mock(SearchIndex.class);
        when(landscape.getSearchIndex()).thenReturn(searchIndex);
        ItemIndex<Item> itemIndex = mock(ItemIndex.class);
        when(landscape.getItems()).thenReturn(itemIndex);
        when(landscape.getKpis()).thenReturn(Map.of());

        //when
        listener.onProcessingFinishedEvent(e);

        //then
        verify(landscape).getSearchIndex();
        verify(landscape).getKpis();
        verify(landscape).applyKPIs(any());
        verify(searchIndex).indexForSearch(eq(landscape), any(Assessment.class));
    }

    @Test
    void onProcessingFinishedEventAssessmentRepoNull() {
        //given
        var assessment = AssessmentFactory.createAssessment(landscape);
        when(assessmentRepository.getAssessment(Mockito.any())).thenReturn(null);
        when(assessmentRepository.createAssessment(Mockito.any())).thenReturn(assessment);
        ProcessingFinishedEvent e = new ProcessingFinishedEvent(new LandscapeDescription("foo"), landscape, new ProcessingChangelog());
        SearchIndex searchIndex = mock(SearchIndex.class);
        when(landscape.getSearchIndex()).thenReturn(searchIndex);
        ItemIndex<Item> itemIndex = mock(ItemIndex.class);
        when(landscape.getItems()).thenReturn(itemIndex);
        when(landscape.getKpis()).thenReturn(Map.of());

        //when
        listener.onProcessingFinishedEvent(e);

        //then
        verify(landscape).getSearchIndex();
        verify(landscape).getKpis();
        verify(landscape).applyKPIs(any());
        verify(searchIndex).indexForSearch(eq(landscape), any(Assessment.class));
    }
}