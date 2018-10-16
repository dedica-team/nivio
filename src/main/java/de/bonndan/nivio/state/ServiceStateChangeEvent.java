package de.bonndan.nivio.state;

import de.bonndan.nivio.landscape.FullyQualifiedIdentifier;
import org.springframework.context.ApplicationEvent;

public class ServiceStateChangeEvent extends ApplicationEvent  {
    private final FullyQualifiedIdentifier fqi;
    private final ServiceState old;
    private final ServiceState current;

    public ServiceStateChangeEvent(Object source, FullyQualifiedIdentifier fqi, ServiceState old, ServiceState state) {
        super(source);
        this.fqi = fqi;
        this.old = old;
        this.current = state;
    }

    public FullyQualifiedIdentifier getFqi() {
        return fqi;
    }

    public ServiceState getOld() {
        return old;
    }

    public ServiceState getCurrent() {
        return current;
    }
}



