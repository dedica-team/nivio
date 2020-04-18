package de.bonndan.nivio;

import java.util.Map;

/**
 * A processing event that occurred because of an error ({@link ProcessingException} present).
 *
 *
 */
public class ProcessingErrorEvent extends ProcessingEvent {

    private final ProcessingException exception;

    public ProcessingErrorEvent(Object source, ProcessingException exception) {
        super(source);
        this.exception = exception;
    }

    public ProcessingException getException() {
        return exception;
    }

    @Override
    public Object getJsonValue() {
        return Map.of(
                "landscape", exception.getLandscape().getIdentifier(),
                "event", getClass().getSimpleName(),
                "error", exception.getMessage()
        );
    }
}
