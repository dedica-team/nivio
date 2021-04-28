package de.bonndan.nivio.search;

import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.model.Item;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Triggers rebuilding of the search index after successful processing.
 *
 *
 */
@Component
public class SearchIndexingEventListener {

    @EventListener(ProcessingFinishedEvent.class)
    public void onProcessingFinishedEvent(@NonNull ProcessingFinishedEvent event) {
        SearchIndex searchIndex = event.getLandscape().getSearchIndex();
        Set<Item> items = event.getLandscape().getItems().all();

        searchIndex.indexForSearch(items);
    }
}
