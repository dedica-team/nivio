package de.bonndan.nivio;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.context.ApplicationEvent;

public abstract class ProcessingEvent extends ApplicationEvent {

    public ProcessingEvent(Object source) {
        super(source);
    }

    @JsonValue
    public abstract Object getJsonValue();
}
