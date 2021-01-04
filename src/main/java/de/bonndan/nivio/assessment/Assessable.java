package de.bonndan.nivio.assessment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bonndan.nivio.ProcessingException;
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
     * Recursively applies the {@link KPI}s to children and self.
     *
     * @param kpis kpis used for assessment
     * @return a map with statusValues indexed by {@link FullyQualifiedIdentifier}
     */
    default Map<FullyQualifiedIdentifier, List<StatusValue>> applyKPIs(final Map<String, KPI> kpis) {
        final Map<FullyQualifiedIdentifier, List<StatusValue>> map = new HashMap<>();

        List<StatusValue> childrenValues = new ArrayList<>();
        //apply to children
        getChildren().stream().map(assessable -> assessable.applyKPIs(kpis)).forEach(fullyQualifiedIdentifierListMap -> {
            map.putAll(fullyQualifiedIdentifierListMap);
            fullyQualifiedIdentifierListMap.forEach((key, value) -> childrenValues.addAll(value));
        });

        //apply each kpi to his
        FullyQualifiedIdentifier fqi = this.getFullyQualifiedIdentifier();
        map.putIfAbsent(fqi, new ArrayList<>());
        kpis.forEach((s, kpi) -> {
            if (!kpi.isEnabled()) {
                return;
            }
            if (!map.containsKey(fqi)) {
                map.put(fqi, new ArrayList<>());
            }
            try {
                map.get(fqi).addAll(kpi.getStatusValues(this));
            } catch (Exception ex) {
                throw new ProcessingException("Failed to apply KPI " + s, ex);
            }

        });

        //add preset status values
        map.get(fqi).addAll(getAdditionalStatusValues());

        childrenValues.addAll(map.get(fqi));
        map.get(fqi).add(StatusValue.summary(SUMMARY_LABEL + "." + getIdentifier(), getWorst(childrenValues)));

        //sort in descending order, worst first
        map.get(fqi).sort(Collections.reverseOrder(new StatusValue.Comparator()));
        return map;
    }

    private StatusValue getWorst(List<StatusValue> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .max(new StatusValue.Comparator())
                .orElse(new StatusValue(SUMMARY_LABEL, Status.UNKNOWN));
    }
}
