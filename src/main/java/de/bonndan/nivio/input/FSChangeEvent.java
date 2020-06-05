package de.bonndan.nivio.input;

import org.springframework.context.ApplicationEvent;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Fired whenever a file changes. The file is not necessarily related to a landscape description.
 */
public class FSChangeEvent extends ApplicationEvent {

    private final WatchEvent<?> event;
    private final Path path;

    public FSChangeEvent(Object source, WatchEvent<?> event, Path path) {
        super(source);
        this.event = event;
        this.path = path;
    }

    public File getChangedFile() {
        return new File(getPath() + "/" + event.context().toString());
    }

    public WatchEvent<?> getEvent() {
        return event;
    }

    public Path getPath() {
        return path;
    }
}
