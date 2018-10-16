package de.bonndan.nivio;

import org.springframework.context.ApplicationEvent;

public class ProcessingErrorEvent extends ApplicationEvent {


    private final ProcessingException exception;

    public ProcessingErrorEvent(Object source, ProcessingException exception) {
        super(source);
        this.exception = exception;
    }

    public ProcessingException getException() {
        return exception;
    }
}
