package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * A basic implementation using injected evaluation functions.
 */
public abstract class AbstractKPI implements KPI {

    protected Function<Assessable, String> valueFunction;
    protected Function<Assessable, String> msgFunction;

    private String description;

    private boolean enabled = true;

    public AbstractKPI() {
    }

    /**
     * @param valueFunction the label which is evaluated for status
     * @param msgFunction   the label which is used as optional message
     */
    public AbstractKPI(@NonNull Function<Assessable, String> valueFunction,
                       @Nullable Function<Assessable, String> msgFunction
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
    public List<StatusValue> getStatusValues(Assessable component) {

        if (valueFunction == null) {
            throw new RuntimeException("Value function not initialized ");
        }
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
