package de.bonndan.nivio.observation;

import org.springframework.context.ApplicationEvent;

import java.net.URL;

/**
 * Event is fired when an input source has changed.
 *
 *
 */
public class InputChangedEvent extends ApplicationEvent {

    private final ObservedChange observedChange;

    public InputChangedEvent(URL url, ObservedChange observedChange) {
        super(url);
        this.observedChange = observedChange;
    }

    @Override
    public URL getSource() {
        return (URL) super.getSource();
    }

    public ObservedChange getObservedChange() {
        return observedChange;
    }
}
