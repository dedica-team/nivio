package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Event is fired after successful indexing of a landscape.
 */
public class ProcessingFinishedEvent extends ProcessingEvent {

    private final LandscapeDescription input;
    private final Landscape landscape;

    /**
     * @param input     the LandscapeDescription input which has been processed
     * @param landscape out
     */
    public ProcessingFinishedEvent(@NonNull final LandscapeDescription input, @NonNull final Landscape landscape) {
        super(Objects.requireNonNull(input).getFullyQualifiedIdentifier());
        this.input = input;
        this.landscape = Objects.requireNonNull(landscape);
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
        return null;
    }
}
