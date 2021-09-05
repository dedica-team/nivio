package de.bonndan.nivio.assessment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Interface for components that can be assessed and can have assigned {@link StatusValue}s.
 */
public interface Assessable {

    /**
     * Returns pre-set status values not computed by {@link KPI}s.
     * <p>
     * This is only for {@link Assessment} and not for public use, since it only contains static data.
     *
     * @return a distinct (by field) set
     */
    @JsonIgnore
    @NonNull
    Set<StatusValue> getAdditionalStatusValues();

    /**
     * Returns the string to be used as key/identifier for a component assessment.
     *
     * @return map key / identifier
     */
    @JsonIgnore
    @NonNull
    String getAssessmentIdentifier();

    /**
     * Returns the components to be assessed before this (e.g. group items).
     */
    @JsonIgnore
    @NonNull
    List<? extends Assessable> getChildren();

    /**
     * Recursively applies the {@link KPI}s to children and self.
     *
     * @param kpis kpis used for assessment
     * @return a map with statusValues indexed by {@link FullyQualifiedIdentifier}
     */
    default Map<String, List<StatusValue>> applyKPIs(final Map<String, KPI> kpis) {
        final Map<String, List<StatusValue>> map = new HashMap<>();

        List<StatusValue> childrenValues = new ArrayList<>();
        //apply to children
        getChildren().stream().map(assessable -> assessable.applyKPIs(kpis)).forEach(fullyQualifiedIdentifierListMap -> {
            map.putAll(fullyQualifiedIdentifierListMap);
            fullyQualifiedIdentifierListMap.forEach((key, value) -> replaceAll(childrenValues, value));
        });

        //apply each kpi to his
        String fqi = this.getAssessmentIdentifier();
        map.putIfAbsent(fqi, new ArrayList<>());

        //add preset status values
        replaceAll(map.get(fqi), getAdditionalStatusValues());

        kpis.forEach((s, kpi) -> {
            if (!kpi.isEnabled()) {
                return;
            }

            try {
                replaceAll(map.get(fqi), kpi.getStatusValues(this));
            } catch (Exception ex) {
                throw new ProcessingException("Failed to apply KPI " + s, ex);
            }

        });

        replaceAll(childrenValues, map.get(fqi));
        List<StatusValue> worst = getWorst(childrenValues);
        replace(map.get(fqi), StatusValue.summary(getAssessmentIdentifier(), worst));

        //sort in descending order, the worst first
        map.get(fqi).sort(Collections.reverseOrder(new StatusValue.Comparator()));
        return map;
    }

    private void replaceAll(List<StatusValue> present, Collection<StatusValue> added) {
        added.forEach(statusValue -> replace(present, statusValue));
    }

    private void replace(List<StatusValue> present, StatusValue added) {
        List<StatusValue> removables = present.stream()
                .filter(statusValue -> statusValue.equals(added))
                .collect(Collectors.toUnmodifiableList());
        present.removeAll(removables);
        present.add(added);
    }

    @NonNull
    static List<StatusValue> getWorst(List<StatusValue> values) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }

        values.sort(new StatusValue.Comparator());
        Status worstStatus = values.get(values.size()-1).getStatus();

        return values.stream().filter(statusValue -> statusValue.getStatus().equals(worstStatus)).collect(Collectors.toUnmodifiableList());
    }
}
