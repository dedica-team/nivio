package de.bonndan.nivio;

import com.fasterxml.jackson.annotation.JsonValue;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;

/**
 * Generic exception tied to a landscape.
 *
 *
 *
 */
public class ProcessingException extends RuntimeException {

    private final Landscape landscape;

    public ProcessingException(Landscape landscape, String message) {
        super(message);
        this.landscape = landscape;
    }

    public ProcessingException(String message, Throwable throwable) {
        super(message, throwable);
        this.landscape = LandscapeDescription.NONE;
    }

    public ProcessingException(Landscape landscape, String message, Throwable throwable) {
        super(message, throwable);
        this.landscape = landscape;
    }

    public static ProcessingException of(Landscape landscape, Throwable throwable) {
        if (throwable instanceof ProcessingException)
            return (ProcessingException) throwable;

        if (throwable instanceof RuntimeException && throwable.getCause() instanceof ProcessingException)
            return (ProcessingException) throwable.getCause();

        return new ProcessingException(landscape, throwable.getMessage(), throwable);
    }

    public Landscape getLandscape() {
        return landscape;
    }

    @JsonValue
    public String getMessage() {
        return super.getMessage();
    }
}
