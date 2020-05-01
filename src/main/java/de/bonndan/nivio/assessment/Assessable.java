package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;

import java.util.*;

import static de.bonndan.nivio.assessment.StatusValue.SUMMARY_LABEL;

/**
 * Interface for components that can be assessed and can have assigned {@link StatusValue}s.
 *
 *
 */
public interface Assessable extends Component {

    /**
     * Returns the highest status as summary of all {@link StatusValue} and children summaries.
     *
     * @return status value, field contains the component identifier, message is the identifier of the highest status value
     */
    default StatusValue getOverallStatus() {

        List<StatusValue> statusValues = new ArrayList<>(getStatusValues());
        getChildren().forEach(o -> statusValues.add(o.getOverallStatus()));

        StatusValue summary = statusValues.stream()
                .filter(Objects::nonNull)
                .max(new StatusValue.Comparator())
                .orElse(new StatusValue(SUMMARY_LABEL, Status.UNKNOWN));

        return new StatusValue(SUMMARY_LABEL + "." + getIdentifier(), summary.getStatus(), summary.getField());
    }

    /**
     * Returns all status value
     *
     * @return a distinct (by field) set
     */
    Set<StatusValue> getStatusValues();

    /**
     * Set/overwrite the status for the assessed field.
     *
     * @param statusValue the new status value
     */
    default void setStatusValue(@NonNull StatusValue statusValue) {

        if (statusValue == null) {
            throw new IllegalArgumentException("Status value is null");
        }

        getStatusValues().add(statusValue);
    }

    /**
     * Returns the components to be assessed before this (e.g. group items).
     *
     */
    default List<? extends Assessable> getChildren() {
        return new ArrayList<>();
    }

    default Map<FullyQualifiedIdentifier, List<StatusValue>> applyKPIs(Map<String, KPI> kpis) {
        Map<FullyQualifiedIdentifier, List<StatusValue>> map = new HashMap<>();
        getChildren().stream().map(assessable -> assessable.applyKPIs(kpis)).forEach(map::putAll);
        kpis.forEach((s, kpi) -> map.put(this.getFullyQualifiedIdentifier(), kpi.getStatusValues(this)));
        return map;
    }
}
