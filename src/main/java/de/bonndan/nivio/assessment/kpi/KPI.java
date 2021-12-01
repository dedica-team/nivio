package de.bonndan.nivio.assessment.kpi;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.output.dto.RangeApiModel;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Key Performance Indicator
 *
 * Used to evaluate {@link Component}s.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface KPI {

    /**
     * Controlled initialisation.
     *
     * @param kpiConfig optional config
     * @throws ProcessingException if initialisation fails (eg regex compiling)
     */
    default void init(@Nullable KPIConfig kpiConfig) {
    }

    /**
     * Returns the status evaluation of the component on the configured field.
     *
     * @param assessable to assess
     * @return current status value, unknown if not present
     */
    @NonNull
    List<StatusValue> getStatusValues(Assessable assessable);

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

    /**
     * Returns the calculated range for the statuses for the API.
     *
     * @return ranges if present, sorted from best to worst, otherwise null
     */
    @Nullable
    Map<Status, RangeApiModel> getRanges();

    /**
     * Returns the calculated matches for the statuses for the API.
     *
     * @return ranges if present, sorted from best to worst, otherwise null
     */
    @Nullable
    Map<Status, List<String>> getMatches();

    @Nullable
    default <T> Map<Status, T> sorted(@Nullable final Map<Status, T> inner) {
        if (inner == null) return null;
        var sorted = new TreeMap<Status, T>(new Status.Comparator());
        inner.forEach(sorted::put);
        return sorted;
    }
}
