package de.bonndan.nivio;

import com.fasterxml.jackson.annotation.JsonValue;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.landscape.LandscapeItem;

/**
 * Generic exception tied to a landscape.
 *
 *
 *
 */
public class ProcessingException extends RuntimeException {

    private final LandscapeItem landscape;

    public ProcessingException(LandscapeItem landscape, String message) {
        super(message);
        this.landscape = landscape;
    }

    public ProcessingException(String message, Throwable throwable) {
        super(message, throwable);
        this.landscape = Environment.NONE;
    }

    public ProcessingException(LandscapeItem landscape, String message, Throwable throwable) {
        super(message, throwable);
        this.landscape = landscape;
    }

    public static ProcessingException of(LandscapeItem landscape, Throwable throwable) {
        if (throwable instanceof ProcessingException)
            return (ProcessingException) throwable;

        if (throwable instanceof RuntimeException && throwable.getCause() instanceof ProcessingException)
            return (ProcessingException) throwable.getCause();

        return new ProcessingException(landscape, throwable.getMessage(), throwable);
    }

    public LandscapeItem getLandscape() {
        return landscape;
    }

    @JsonValue
    public String getMessage() {
        return super.getMessage();
    }
}
