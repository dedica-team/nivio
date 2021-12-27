package de.bonndan.nivio.assessment;

import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingEvent;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class AssessmentChangedEvent extends ProcessingEvent {

    private final Landscape landscape;
    private final Assessment assessment;
    private final ProcessingChangelog changelog;

    public AssessmentChangedEvent(@NonNull final Landscape landscape,
                                  @NonNull final Assessment assessment,
                                  @NonNull final ProcessingChangelog changes
    ) {
        super(Objects.requireNonNull(landscape).getFullyQualifiedIdentifier());
        this.landscape = landscape;
        this.assessment = Objects.requireNonNull(assessment);
        this.changelog = changes;
    }

    @Override
    public String getLevel() {
        return ProcessingEvent.LOG_LEVEL_INFO;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return null;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public ProcessingChangelog getChangelog() {
        return changelog;
    }
}
