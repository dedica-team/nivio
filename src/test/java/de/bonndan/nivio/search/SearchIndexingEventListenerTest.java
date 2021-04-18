package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchIndexingEventListenerTest {

    private Landscape landscape;
    private SearchIndexingEventListener listener;

    @BeforeEach
    void setUp() {
        landscape = mock(Landscape.class);
        listener = new SearchIndexingEventListener();
    }

    @Test
    void onProcessingFinishedEvent() {
        //given
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