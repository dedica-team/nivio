package de.bonndan.nivio.search;

import de.bonndan.nivio.assessment.AssessmentRepository;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
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

    @EventListener(ProcessingFinishedEvent.class)
    public void onProcessingFinishedEvent(@NonNull ProcessingFinishedEvent event) {
        final Landscape landscape = event.getLandscape();
        final SearchIndex searchIndex = landscape.getSearchIndex();

        //see https://github.com/dedica-team/nivio/issues/519
        var assessment = assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier());
        if (assessment == null) {
            assessment = assessmentRepository.createAssessment(landscape);
        }
        searchIndex.indexForSearch(landscape, assessment);
    }
}
