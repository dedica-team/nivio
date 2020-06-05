package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Component;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Key Performance Indicator
 *
 * Used to evaluate {@link Component}s.
 */
public interface KPI {

    /**
     * Controlled initialisation.
     *
     * @throws ProcessingException if initialisation fails (eg regex compiling)
     */
    default void init() {}

    /**
     * Returns the status evaluation of the component on the configured field.
     *
     * @param component to assess
     * @return current status value, unknown if not present
     */
    @NonNull
    List<StatusValue> getStatusValues(Component component);

    /**
     * Describes the meaning of the KPI.
     */
    String getDescription();
}
