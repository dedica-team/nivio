package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AssessmentFactory {
    public static final String errorMessageAssessmentNull = "Assessments can't be created from a null value";

    private AssessmentFactory() {
    }

    /**
     * This method which generates a new Assessment from a landscape.
     *
     * @param landscape used to generate the new Assessment with its own kpis.
     * @return Assessment
     * @throws NullPointerException On null input.
     */
    public static Assessment createAssessment(@NonNull Landscape landscape) {
        var testedLandscape = Objects.requireNonNull(landscape, errorMessageAssessmentNull);
        return new Assessment(testedLandscape.applyKPIs(testedLandscape.getKpis()));
    }

    /**
     * This method which generates a new  from a landscape and a kpi.
     *
     * @param landscape used to generate the new Assessment.
     * @param kpis      external kpi used to get status values.
     * @return Assessment
     * @throws NullPointerException On null input.
     */
    public static Assessment createAssessment(@NonNull Landscape landscape, @NonNull Map<String, KPI> kpis) {
        var testedLandscape = Objects.requireNonNull(landscape, errorMessageAssessmentNull);
        var testedKpis = Objects.requireNonNull(kpis, errorMessageAssessmentNull);
        var map = new HashMap<FullyQualifiedIdentifier, List<StatusValue>>();
        testedKpis.forEach((k, v) -> map.putIfAbsent(FullyQualifiedIdentifier.from(k), v.getStatusValues(testedLandscape)));
        return new Assessment(map);
    }

    /**
     * This method which generates a new Assessment from map.
     *
     * @param results used to generate the new Assessment with fully qualified identifier and a list of status values.
     * @return Assessment
     * @throws NullPointerException On null input.
     */
    public static Assessment createAssessment(@NonNull Map<FullyQualifiedIdentifier, List<StatusValue>> results) {
        var testedResult = Objects.requireNonNull(results, errorMessageAssessmentNull);
        return new Assessment(testedResult);
    }


}
