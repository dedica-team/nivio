package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AssessmentFactory {

    static final String ASSESSMENT_ERROR_NULL = "Assessments can't be created from a null value";

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
        var testedLandscape = Objects.requireNonNull(landscape, ASSESSMENT_ERROR_NULL);
        return new Assessment(testedLandscape.applyKPIs(testedLandscape.getKpis()));
    }

    /**
     * This method which generates a new assessment from a landscape and a kpi.
     *
     * @param landscape used to generate the new Assessment.
     * @param kpis      external kpi used to get status values.
     * @return Assessment
     * @throws NullPointerException On null input.
     */
    @NonNull
    public static Assessment createAssessment(@NonNull final Landscape landscape, @NonNull Map<String, KPI> kpis) {
        var testedLandscape = Objects.requireNonNull(landscape, ASSESSMENT_ERROR_NULL);
        var testedKpis = Objects.requireNonNull(kpis, ASSESSMENT_ERROR_NULL);
        var map = new HashMap<String, List<StatusValue>>();
        testedKpis.forEach((k, v) -> map.putIfAbsent(k, v.getStatusValues(testedLandscape)));
        return new Assessment(map);
    }

    /**
     * This method which generates a new Assessment from map.
     *
     * @param results used to generate the new Assessment with fully qualified identifier and a list of status values.
     * @return Assessment
     * @throws NullPointerException On null input.
     */
    @NonNull
    public static Assessment createAssessment(@NonNull Map<String, List<StatusValue>> results) {
        return new Assessment(Objects.requireNonNull(results, ASSESSMENT_ERROR_NULL));
    }


}
