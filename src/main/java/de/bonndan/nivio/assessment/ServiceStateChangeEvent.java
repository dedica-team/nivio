package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.assessment.StatusValue;
import org.springframework.context.ApplicationEvent;

/**
 * Refactor to assessment change event, or delete
 * https://github.com/dedica-team/nivio/issues/545
 */
public class ServiceStateChangeEvent extends ApplicationEvent  {
    private final FullyQualifiedIdentifier fqi;
    private final StatusValue old;
    private final StatusValue current;

    public ServiceStateChangeEvent(Object source, FullyQualifiedIdentifier fqi, StatusValue old, StatusValue state) {
        super(source);
        this.fqi = fqi;
        this.old = old;
        this.current = state;
    }

    public FullyQualifiedIdentifier getFqi() {
        return fqi;
    }

    public StatusValue getOld() {
        return old;
    }

    public StatusValue getCurrent() {
        return current;
    }
}



