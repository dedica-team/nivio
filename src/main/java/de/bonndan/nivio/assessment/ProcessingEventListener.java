package de.bonndan.nivio.assessment;

import de.bonndan.nivio.input.ProcessingFinishedEvent;
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
    private final ApplicationEventPublisher publisher;

    public ProcessingEventListener(final AssessmentRepository repository, final ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @EventListener(ProcessingFinishedEvent.class)
    public void onProcessingFinishedEvent(final ProcessingFinishedEvent processingEvent) {
        Landscape landscape = processingEvent.getLandscape();
        Assessment assessment = AssessmentFactory.createAssessment(landscape);
        repository.save(landscape.getFullyQualifiedIdentifier(), assessment);
        publisher.publishEvent(new AssessmentChangedEvent(landscape, assessment));
    }

}
