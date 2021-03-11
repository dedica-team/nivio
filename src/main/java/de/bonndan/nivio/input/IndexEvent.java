package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;

import java.util.Objects;

/**
 * Event is fired to (re)index a landscape.
 */
public class IndexEvent extends ProcessingEvent {

    private final String message;
    private final LandscapeDescription landscapeDescription;

    /**
     * @param landscapeDescription landscape description
     * @param message message for the UI
     */
    public IndexEvent(LandscapeDescription landscapeDescription, String message) {
        super(Objects.requireNonNull(landscapeDescription, "The IndexEvent must contain a landscape description.").getFullyQualifiedIdentifier());

        this.landscapeDescription = landscapeDescription;
        this.message = message;
    }

    /**
     * @return the {@link LandscapeDescription}, not the landscape!
     */
    public LandscapeDescription getLandscapeDescription() {
        return landscapeDescription;
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
        return message;
    }
}
