package de.bonndan.nivio.notification;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.StatusItem;
import org.springframework.context.ApplicationEvent;

//TODO use
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



