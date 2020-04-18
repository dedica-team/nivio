package de.bonndan.nivio;

import de.bonndan.nivio.model.Landscape;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Event is fired after successful indexing of a landscape.
 *
 *
 *
 */
public class ProcessingFinishedEvent extends ProcessingEvent {

    private final Landscape landscape;

    public ProcessingFinishedEvent(Object source, Landscape landscape) {
        super(source);
        this.landscape = landscape;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    @Override
    public Object getJsonValue() {
        return Map.of(
                "landscape", landscape.getIdentifier(),
                "event", getClass().getSimpleName()
        );
    }
}
