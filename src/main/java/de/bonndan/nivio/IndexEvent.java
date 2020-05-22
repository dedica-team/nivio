package de.bonndan.nivio;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;

/**
 * Event is fired to (re)index a landscape.
 *
 *
 *
 */
public class IndexEvent extends ProcessingEvent {

    private final Landscape landscape;
    private final String message;

    public IndexEvent(Object source, Landscape landscape, String message) {
        super(source);
        if (!(landscape instanceof LandscapeDescription)) {
            throw new IllegalArgumentException("The IndexEvent must contain a landscape description.");
        }
        this.landscape = landscape;
        this.message = message;
    }

    /**
     * @return the {@link LandscapeDescription}, not the landscape!
     */
    @Override
    public Landscape getLandscape() {
        return landscape;
    }

    @Override
    public String getLevel() {
        return "info";
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
