package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Component;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * A basic implementation using injected evaluation functions.
 *
 */
public abstract class AbstractKPI implements KPI {

    private final Function<Component, String> valueFunction;
    private final Function<Component, String> msgFunction;
    private String description;

    private boolean enabled = true;

    /**
     * @param valueFunction the label which is evaluated for status
     * @param msgFunction   the label which is used as optional message
     */
    public AbstractKPI(@NonNull Function<Component, String> valueFunction,
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
    public List<StatusValue> getStatusValues(Component component) {

        String value = valueFunction.apply(component);
        String message = msgFunction != null ? msgFunction.apply(component) : null;
        return getStatusValues(value, message);
    }

    /**
     * Returns the status values
     *
     * @param value   the value to assess. Can be null if no value is present or the KPI is not applicable.
     * @param message the optional message
     * @return a status value if assessed
     */
    protected abstract List<StatusValue> getStatusValues(@Nullable String value, @Nullable String message);

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
