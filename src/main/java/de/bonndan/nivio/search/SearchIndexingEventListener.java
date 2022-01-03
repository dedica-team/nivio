package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.AssessmentChangedEvent;
import de.bonndan.nivio.assessment.AssessmentRepository;
import de.bonndan.nivio.model.Landscape;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Triggers rebuilding of the search index after successful processing.
 */
@Component
public class SearchIndexingEventListener {

    private final AssessmentRepository assessmentRepository;

    public SearchIndexingEventListener(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    @EventListener(AssessmentChangedEvent.class)
    public void onProcessingFinishedEvent(@NonNull final AssessmentChangedEvent event) {
        final Landscape landscape = event.getLandscape();
        final SearchIndex searchIndex = landscape.getSearchIndex();

        assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier())
                .ifPresent(assessment -> searchIndex.indexForSearch(landscape, assessment));
    }
}
