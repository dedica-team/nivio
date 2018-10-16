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

    public static ProcessingException of(LandscapeInterface landscape, Throwable throwable) {
        if (throwable instanceof ProcessingException)
            return (ProcessingException) throwable;

        if (throwable instanceof RuntimeException && throwable.getCause() instanceof ProcessingException)
            return (ProcessingException) throwable.getCause();

        return new ProcessingException(landscape, throwable.getMessage(), throwable);
    }

    public LandscapeInterface getLandscape() {
        return landscape;
    }
}
