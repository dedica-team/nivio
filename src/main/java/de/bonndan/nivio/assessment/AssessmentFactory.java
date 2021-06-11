package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Landscape;

import java.util.List;
import java.util.Map;

/**
 * A factory to create Assessment instances from map or Landscape
 */

public class AssessmentFactory {

    public static Assessment createAssessment(Map<FullyQualifiedIdentifier, List<StatusValue>> results) {
        return new Assessment(results);
    }

    public static Assessment createAssessment(Landscape landscape) {
        return new Assessment(landscape.applyKPIs(landscape.getKpis()));
    }
}
