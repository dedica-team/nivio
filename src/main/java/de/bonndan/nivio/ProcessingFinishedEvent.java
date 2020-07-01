package de.bonndan.nivio;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;

/**
 * Event is fired after successful indexing of a landscape.
 *
 *
 *
 */
public class ProcessingFinishedEvent extends ProcessingEvent {

    private final Landscape landscape;

    /**
     * @param source TODO is this a misuse of event source? We store the LandscapeDescription instead of the triggerer
     * @param landscape out
     */
    public ProcessingFinishedEvent(LandscapeDescription source, Landscape landscape) {
        super(source);
        this.landscape = landscape;
    }

    @Override
    public Landscape getLandscape() {
        return landscape;
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
