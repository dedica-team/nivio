package de.bonndan.nivio.input;

import org.springframework.context.ApplicationEvent;

import java.nio.file.WatchEvent;

public class FSChangeEvent extends ApplicationEvent {

    private final WatchEvent<?> event;

    public FSChangeEvent(Object source, WatchEvent<?> event) {
        super(source);
        this.event = event;
    }

    public WatchEvent<?> getEvent() {
        return event;
    }
}
