package de.bonndan.nivio;

import com.fasterxml.jackson.annotation.JsonValue;
import de.bonndan.nivio.input.dto.LandscapeDescription;

/**
 * Generic exception tied to a landscape.
 *
 *
 *
 */
public class ProcessingException extends RuntimeException {

    private final LandscapeDescription landscapeDescription;

    public ProcessingException(LandscapeDescription landscapeDescription, String message) {
        super(message);
        this.landscapeDescription = landscapeDescription;
    }

    public ProcessingException(String message, Throwable throwable) {
        super(message, throwable);
        this.landscapeDescription = LandscapeDescription.NONE;
    }

    public ProcessingException(LandscapeDescription landscape, String message, Throwable throwable) {
        super(message, throwable);
        this.landscapeDescription = landscape;
    }

    public static ProcessingException of(LandscapeDescription landscape, Throwable throwable) {
        if (throwable instanceof ProcessingException)
            return (ProcessingException) throwable;

        if (throwable instanceof RuntimeException && throwable.getCause() instanceof ProcessingException)
            return (ProcessingException) throwable.getCause();

        return new ProcessingException(landscape, throwable.getMessage(), throwable);
    }

    public LandscapeDescription getLandscapeDescription() {
        return landscapeDescription;
    }

    @JsonValue
    public String getMessage() {
        return super.getMessage();
    }
}
