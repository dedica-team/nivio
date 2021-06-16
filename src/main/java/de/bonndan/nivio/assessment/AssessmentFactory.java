package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A factory to create Assessment instances from map or Landscape
 */

public class AssessmentFactory {
    private AssessmentFactory() {
    }

    public static Assessment createAssessment(@NonNull Landscape landscape, @NonNull Map<String, KPI> kpis) {
        var map = new HashMap<FullyQualifiedIdentifier, List<StatusValue>>();
        kpis.forEach((k, v) -> map.putIfAbsent(FullyQualifiedIdentifier.from(k), v.getStatusValues(landscape)));
        return new Assessment(map);
    }

    public static Assessment createAssessment(@NonNull Map<FullyQualifiedIdentifier, List<StatusValue>> results) {
        return new Assessment(results);
    }

    public static Assessment createAssessment(@NonNull Landscape landscape) {
        return new Assessment(landscape.applyKPIs(landscape.getKpis()));
    }
}
