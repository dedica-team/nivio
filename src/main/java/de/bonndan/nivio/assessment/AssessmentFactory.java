package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A factory to create Assessment instances from map or Landscape
 */
public class AssessmentFactory {
    private AssessmentFactory() {
    }

    public static Assessment createAssessment(@NonNull Landscape landscape) {
        var testedLandscape = Objects.requireNonNull(landscape, "Assessments can't be created from a null value");
        return new Assessment(testedLandscape.applyKPIs(testedLandscape.getKpis()));
    }

    public static Assessment createAssessment(@NonNull Landscape landscape, @NonNull Map<String, KPI> kpis) {
        var testedLandscape = Objects.requireNonNull(landscape, "Assessments can't be created from a null value");
        var testedKpis = Objects.requireNonNull(kpis, "Assessments can't be created from a null value");
        var map = new HashMap<FullyQualifiedIdentifier, List<StatusValue>>();
        testedKpis.forEach((k, v) -> map.putIfAbsent(FullyQualifiedIdentifier.from(k), v.getStatusValues(testedLandscape)));
        return new Assessment(map);
    }

    public static Assessment createAssessment(@NonNull Map<FullyQualifiedIdentifier, List<StatusValue>> results) {
        var testedResult = Objects.requireNonNull(results, "Assessments can't be created from a null value");
        return new Assessment(testedResult);
    }


}
