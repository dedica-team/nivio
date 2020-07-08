package de.bonndan.nivio.assessment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bonndan.nivio.assessment.kpi.AbstractKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;

import java.util.*;

import static de.bonndan.nivio.assessment.StatusValue.SUMMARY_LABEL;

/**
 * Interface for components that can be assessed and can have assigned {@link StatusValue}s.
 */
public interface Assessable extends Component {

    /**
     * Returns the highest status as summary of all {@link StatusValue} and children summaries.
     *
     * @return status value, field contains the component identifier, message is the identifier of the highest status value
     */
    @JsonIgnore
    default StatusValue getOverallStatus() {

        List<StatusValue> statusValues = new ArrayList<>(getAdditionalStatusValues());
        getChildren().forEach(o -> statusValues.add(o.getOverallStatus()));

        StatusValue summary = statusValues.stream()
                .filter(Objects::nonNull)
                .max(new StatusValue.Comparator())
                .orElse(new StatusValue(SUMMARY_LABEL, Status.UNKNOWN));

        return new StatusValue(SUMMARY_LABEL + "." + getIdentifier(), summary.getStatus(), summary.getField());
    }

    /**
     * Returns pre-set status values not computed by {@link KPI}s.
     * <p>
     * This is only for {@link Assessment} and not for public use, since it only contains static data.
     *
     * @return a distinct (by field) set
     */
    @JsonIgnore
    Set<StatusValue> getAdditionalStatusValues();

    /**
     * Returns the components to be assessed before this (e.g. group items).
     */
    @JsonIgnore
    default List<? extends Assessable> getChildren() {
        return new ArrayList<>();
    }

    /**
     * Recursively applies the {@link AbstractKPI}s to children and self.
     *
     * @param kpis kpis used for assessment
     * @return a map with statusValues indexed by {@link FullyQualifiedIdentifier}
     */
    default Map<FullyQualifiedIdentifier, List<StatusValue>> applyKPIs(final Map<String, KPI> kpis) {
        final Map<FullyQualifiedIdentifier, List<StatusValue>> map = new HashMap<>();

        //apply to children
        getChildren().stream().map(assessable -> assessable.applyKPIs(kpis)).forEach(map::putAll);

        //apply each kpi to his
        FullyQualifiedIdentifier fqi = this.getFullyQualifiedIdentifier();
        kpis.forEach((s, kpi) -> {
            if (!kpi.isEnabled()) {
                return;
            }
            if (!map.containsKey(fqi)) {
                map.put(fqi, new ArrayList<>());
            }
            map.get(fqi).addAll(kpi.getStatusValues(this));
        });

        Set<StatusValue> additionalStatusValues = getAdditionalStatusValues();
        if (additionalStatusValues.size() > 0) {
            if (!map.containsKey(fqi)) {
                map.put(fqi, new ArrayList<>());
            }
            map.get(fqi).addAll(additionalStatusValues);
        }
        return map;
    }
}
