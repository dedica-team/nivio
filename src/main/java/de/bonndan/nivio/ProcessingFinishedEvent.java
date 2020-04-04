package de.bonndan.nivio;

import de.bonndan.nivio.model.Landscape;
import org.springframework.context.ApplicationEvent;

/**
 * Event is fired after successful indexing of a landscape.
 *
 *
 *
 */
public class ProcessingFinishedEvent extends ApplicationEvent {


    private final Landscape landscape;

    public ProcessingFinishedEvent(Object source, Landscape landscape) {
        super(source);
        this.landscape = landscape;
    }

    public Landscape getLandscape() {
        return landscape;
    }
}
