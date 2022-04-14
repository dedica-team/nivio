package de.bonndan.nivio.assessment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Interface for components that can be assessed and can have assigned {@link StatusValue}s.
 */
public interface Assessable {

    URI getFullyQualifiedIdentifier();

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
     * Returns the components to be assessed before this (e.g. group items).
     */
    @JsonIgnore
    @NonNull
    Set<Assessable> getAssessables();

    /**
     * Recursively applies the {@link KPI}s to children and self.
     *
     * @param kpis kpis used for assessment
     * @return a map with statusValues indexed by {@link FullyQualifiedIdentifier}
     */
    default Map<URI, List<StatusValue>> applyKPIs(@NonNull final Map<String, KPI> kpis) {
        final Map<URI, List<StatusValue>> map = new HashMap<>();

        List<StatusValue> childrenValues = new ArrayList<>();
        //apply to children
        getAssessables().stream().map(assessable -> assessable.applyKPIs(kpis)).forEach(fullyQualifiedIdentifierListMap -> {
            map.putAll(fullyQualifiedIdentifierListMap);
            fullyQualifiedIdentifierListMap.forEach((key, value) -> replaceAll(childrenValues, value));
        });

        //apply each kpi to his
        URI fqi = this.getFullyQualifiedIdentifier();
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
        replace(map.get(fqi), StatusValue.summary(getFullyQualifiedIdentifier(), childrenValues));

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
        Status worstStatus = values.get(values.size() - 1).getStatus();

        return values.stream().filter(statusValue -> statusValue.getStatus().equals(worstStatus)).collect(Collectors.toUnmodifiableList());
    }
}
