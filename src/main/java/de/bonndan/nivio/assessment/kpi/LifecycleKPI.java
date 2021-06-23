package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;

import java.util.*;

/**
 * This KPI evaluates the lifecycle label for "official" values (see {@link Lifecycle}).
 */
public class LifecycleKPI extends CustomKPI {

    public static final String IDENTIFIER = Label.lifecycle.name();

    public LifecycleKPI() {
        super();
        label = Label.lifecycle.name();
        msgFunction = component -> Optional.ofNullable(Lifecycle.from(valueFunction.apply(component)))
                .map(lifecycle -> "Phase: " + lifecycle.name().toLowerCase(Locale.ROOT).replace("_", " "))
                .orElse("unknown");

        setDescription("This KPI evaluates the lifecycle label for known values (PLANNED, PRODUCTION).");
    }

    @Override
    protected List<StatusValue> getStatusValues(String value, String message) {
        Lifecycle lifecycle = Lifecycle.from(value);
        if (Lifecycle.PRODUCTION.equals(lifecycle)) {
            return Collections.singletonList(new StatusValue(Label.lifecycle.name(), Status.GREEN, message));
        }
        if (Lifecycle.END_OF_LIFE.equals(lifecycle)) {
            return Collections.singletonList(new StatusValue(Label.lifecycle.name(), Status.ORANGE, message));
        }

        return new ArrayList<>();
    }
}
