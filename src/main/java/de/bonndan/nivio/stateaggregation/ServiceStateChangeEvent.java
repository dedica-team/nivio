package de.bonndan.nivio.stateaggregation;

import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import de.bonndan.nivio.landscape.StatusItem;
import org.springframework.context.ApplicationEvent;

public class ServiceStateChangeEvent extends ApplicationEvent  {
    private final FullyQualifiedIdentifier fqi;
    private final StatusItem old;
    private final StatusItem current;

    public ServiceStateChangeEvent(Object source, FullyQualifiedIdentifier fqi, StatusItem old, StatusItem state) {
        super(source);
        this.fqi = fqi;
        this.old = old;
        this.current = state;
    }

    public FullyQualifiedIdentifier getFqi() {
        return fqi;
    }

    public StatusItem getOld() {
        return old;
    }

    public StatusItem getCurrent() {
        return current;
    }
}



