package de.bonndan.nivio.assessment;

import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Creates a new assessment after a {@link ProcessingFinishedEvent}
 */
@Component
public class ProcessingEventListener {

    private final AssessmentRepository repository;
    private final AssessmentFactory factory;
    private final ApplicationEventPublisher publisher;

    public ProcessingEventListener(final AssessmentRepository repository,
                                   final AssessmentFactory factory,
                                   final ApplicationEventPublisher publisher
    ) {
        this.repository = repository;
        this.factory = factory;
        this.publisher = publisher;
    }

    @EventListener(ProcessingFinishedEvent.class)
    public void onProcessingFinishedEvent(final ProcessingFinishedEvent processingEvent) {
        Landscape landscape = processingEvent.getLandscape();
        FullyQualifiedIdentifier fqi = landscape.getFullyQualifiedIdentifier();
        var current = repository.getAssessment(fqi);

        Assessment assessment = factory.createAssessment(landscape);
        repository.save(fqi, assessment);

        ProcessingChangelog changes = current
                .map(assessment1 -> AssessmentChangelogFactory.getChanges(landscape, assessment1, assessment))
                .orElseGet(() -> AssessmentChangelogFactory.getChanges(landscape, assessment));

        if (!changes.getChanges().isEmpty()) {
            publisher.publishEvent(new AssessmentChangedEvent(landscape, changes));
        }
    }

}
