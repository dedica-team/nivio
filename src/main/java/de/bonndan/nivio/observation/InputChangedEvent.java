package de.bonndan.nivio.observation;

import org.springframework.context.ApplicationEvent;

/**
 * Event is fired when an input source has changed.
 *
 *
 */
public class InputChangedEvent extends ApplicationEvent {

    public InputChangedEvent(ObservedChange source) {
        super(source);
    }

    @Override
    public ObservedChange getSource() {
        return (ObservedChange) super.getSource();
    }
}
