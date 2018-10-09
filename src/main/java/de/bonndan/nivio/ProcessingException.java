package de.bonndan.nivio;

import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.landscape.LandscapeInterface;

/**
 * Generic exception tied to a landscape.
 *
 *
 *
 */
public class ProcessingException extends RuntimeException {

    private final LandscapeInterface landscape;

    public ProcessingException(LandscapeInterface landscape, String message) {
        super(message);
        this.landscape = landscape;
    }

    public ProcessingException(String message, Throwable throwable) {
        super(message, throwable);
        this.landscape = Environment.NONE;
    }

    public ProcessingException(LandscapeInterface landscape, String message, Throwable throwable) {
        super(message, throwable);
        this.landscape = landscape;
    }

    public LandscapeInterface getLandscape() {
        return landscape;
    }
}
