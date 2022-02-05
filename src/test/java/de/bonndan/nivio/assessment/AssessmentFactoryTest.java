package de.bonndan.nivio.assessment;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.kpi.ConditionKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;

import static de.bonndan.nivio.assessment.AssessmentFactory.ASSESSMENT_ERROR_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AssessmentFactoryTest {


    private AssessmentFactory factory;
    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        factory = new AssessmentFactory();
        graph = new GraphTestSupport();
    }

    @Test
    void getAssessmentFromFactoryLandscapeAndKPI() {

        var conditionKpi = new ConditionKPI();
        var map = new HashMap<String, KPI>();
        map.put("test", conditionKpi);
        var assessment = factory.createAssessment(graph.landscape, map);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }

    @Test
    void getAssessmentFromFactoryLandscape() {

        var assessment = factory.createAssessment(graph.landscape);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }

    @Test
    void getAssessmentFromFactoryMap() {
        var fqi = FullyQualifiedIdentifier.build(Landscape.class,"l1", "u1", "c1", "g1", "i1", "p1");
        var statusList = new ArrayList<StatusValue>();
        var results = new HashMap<URI, List<StatusValue>>();
        results.put(fqi, statusList);
        var assessment = factory.createAssessment(results);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }


    @Test
    void testNullValues() {
        var exception = assertThrows(NullPointerException.class, () -> factory.createAssessment(null, null));
        assertThat(exception.getMessage()).isEqualTo(ASSESSMENT_ERROR_NULL);
        exception = assertThrows(NullPointerException.class, () -> factory.createAssessment((Landscape) null));
        assertThat(exception.getMessage()).isEqualTo(ASSESSMENT_ERROR_NULL);
        exception = assertThrows(NullPointerException.class, () -> factory.createAssessment((Map<URI, List<StatusValue>>) null));
        assertThat(exception.getMessage()).isEqualTo(ASSESSMENT_ERROR_NULL);
    }
}