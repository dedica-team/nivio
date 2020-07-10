package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Component;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
     * @param kpiConfig optional config
     */
    default void init(@Nullable KPIConfig kpiConfig) {}

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

    /**
     * Flag showing whether KPI should be used in assessment.
     *
     * @return true if active
     */
    boolean isEnabled();
}
