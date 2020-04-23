package de.bonndan.nivio;

import de.bonndan.nivio.model.Landscape;

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
    public Landscape getLandscape() {
        return exception.getLandscape();
    }

    @Override
    public String getLevel() {
        return "error";
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return exception.getMessage();
    }
}
