package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.Objects;

/**
 * Event is fired after successful indexing of a landscape.
 */
public class ProcessingFinishedEvent extends ProcessingEvent {

    @NonNull
    private final LandscapeDescription input;

    @NonNull
    private final Landscape landscape;

    @NonNull
    private final ProcessingChangelog changelog;

    /**
     * @param input     the LandscapeDescription input which has been processed
     * @param landscape out
     * @param changelog log of component changes
     * @throws NullPointerException if any of the params is null
     */
    public ProcessingFinishedEvent(@NonNull final LandscapeDescription input,
                                   @NonNull final Landscape landscape,
                                   @NonNull final ProcessingChangelog changelog
    ) {
        super(Objects.requireNonNull(input).getFullyQualifiedIdentifier());
        this.input = input;
        this.landscape = Objects.requireNonNull(landscape);
        this.changelog = Objects.requireNonNull(changelog);
    }

    @NonNull
    public Landscape getLandscape() {
        return landscape;
    }

    @NonNull
    public LandscapeDescription getInput() {
        return input;
    }

    @Override
    public String getLevel() {
        return LOG_LEVEL_INFO;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return "Processing of input data has finished.";
    }

    @NonNull
    public ProcessingChangelog getChangelog() {
        return changelog;
    }

    @Override
    public URI getSource() {
        return landscape.getFullyQualifiedIdentifier();
    }
}
