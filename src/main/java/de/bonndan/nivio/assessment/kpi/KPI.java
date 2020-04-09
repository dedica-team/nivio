package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Component;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * A key performance indicator related to a landscape component.
 *
 *
 */
public abstract class KPI {

    private final Function<Component, String> valueFunction;
    private final Function<Component, String> msgFunction;
    private String description;

    /**
     * @param valueFunction the label which is evaluated for status
     * @param msgFunction   the label which is used as optional message
     */
    public KPI(@NonNull Function<Component, String> valueFunction,
               @Nullable Function<Component, String> msgFunction
    ) {
        this.valueFunction = valueFunction;
        this.msgFunction = msgFunction;
    }

    /**
     * Returns the status evaluation of the component on the configured field.
     *
     * @param component to assess
     * @return current status value, unknown if not present
     */
    @NonNull
    public StatusValue getStatusValue(Component component) {

        String value = valueFunction.apply(component);
        String message = msgFunction != null ? msgFunction.apply(component) : null;
        Optional<StatusValue> evaluated = getStatusValue(value, message);

        return evaluated.orElse(new StatusValue(value, Status.UNKNOWN, message));
    }

    /**
     *
     * @param value the value to assess. Can be null if no value is present or the KPI is not applicable.
     * @param message the optional message
     * @return a status value if assessed
     */
    protected abstract Optional<StatusValue> getStatusValue(@Nullable String value, @Nullable String message);

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
