package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AssessmentFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentFactory.class);

    static final String ASSESSMENT_ERROR_NULL = "Assessments can't be created from a null value";

    /**
     * This method which generates a new Assessment from a landscape.
     *
     * @param landscape used to generate the new Assessment with its own kpis.
     * @return Assessment
     * @throws NullPointerException On null input.
     */
    public Assessment createAssessment(@NonNull final Landscape landscape) {
        var testedLandscape = Objects.requireNonNull(landscape, ASSESSMENT_ERROR_NULL);
        LOGGER.info("Creating assessment for landscape {}", landscape.getIdentifier());
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
    public Assessment createAssessment(@NonNull final Landscape landscape, @NonNull final Map<String, KPI> kpis) {
        var testedLandscape = Objects.requireNonNull(landscape, ASSESSMENT_ERROR_NULL);
        var testedKpis = Objects.requireNonNull(kpis, ASSESSMENT_ERROR_NULL);
        var map = new HashMap<URI, List<StatusValue>>();
        testedKpis.forEach((k, kpi) -> map.putIfAbsent(testedLandscape.getFullyQualifiedIdentifier(), kpi.getStatusValues(testedLandscape)));
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
    public Assessment createAssessment(@NonNull final Map<URI, List<StatusValue>> results) {
        return new Assessment(Objects.requireNonNull(results, ASSESSMENT_ERROR_NULL));
    }

}
