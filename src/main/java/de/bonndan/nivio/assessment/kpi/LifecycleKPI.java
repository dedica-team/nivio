package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import org.springframework.lang.NonNull;

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
                .map(lifecycle -> "Phase: " + lifecycle.name().replace("_", " "))
                .orElse("unknown");

        matchers = Map.of(
                Status.GREEN, List.of(Lifecycle.PRODUCTION.name()),
                Status.ORANGE, List.of(Lifecycle.END_OF_LIFE.name().replace("_", " "))
        );

        setDescription("Evaluates the lifecycle label for known values (e.g. PLANNED, PRODUCTION, etc.).");
    }

    @Override
    protected List<StatusValue> getStatusValues(@NonNull final Assessable assessable, String value, String message) {
        var lifecycle = Lifecycle.from(value);
        if (Lifecycle.PRODUCTION.equals(lifecycle)) {
            return Collections.singletonList(new StatusValue(assessable.getAssessmentIdentifier(), Label.lifecycle.name(), Status.GREEN, message));
        }
        if (Lifecycle.END_OF_LIFE.equals(lifecycle)) {
            return Collections.singletonList(new StatusValue(assessable.getAssessmentIdentifier(), Label.lifecycle.name(), Status.ORANGE, message));
        }

        return new ArrayList<>();
    }
}
