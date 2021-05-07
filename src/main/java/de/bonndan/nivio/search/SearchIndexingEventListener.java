package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
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
        final Landscape landscape = event.getLandscape();
        final SearchIndex searchIndex = landscape.getSearchIndex();

        //see https://github.com/dedica-team/nivio/issues/519
        Assessment assessment = new Assessment(landscape.applyKPIs(landscape.getKpis()));
        searchIndex.indexForSearch(landscape, assessment);
    }
}
