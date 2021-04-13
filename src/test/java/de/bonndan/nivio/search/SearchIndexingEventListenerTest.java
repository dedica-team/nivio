package de.bonndan.nivio.search;

import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Set<Item> aSet = Set.of(ItemFactory.getTestItem("a", "b"));
        when(itemIndex.all()).thenReturn(aSet);

        //when
        listener.onProcessingFinishedEvent(e);

        //then
        verify(landscape).getSearchIndex();
        verify(landscape).getItems();
        verify(searchIndex).indexForSearch(eq(aSet));
    }
}