package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.ConditionKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.bonndan.nivio.assessment.AssessmentFactory.ASSESSMENT_ERROR_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AssessmentFactoryTest {


    private AssessmentFactory factory;

    @BeforeEach
    void setup() {
        factory = new AssessmentFactory();
    }

    @Test
    void getAssessmentFromFactoryLandscapeAndKPI() {
        var foo = ItemFactory.getTestItem("a", "foo");
        var bar = ItemFactory.getTestItem("b", "bar");
        var landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();
        var conditionKpi = new ConditionKPI();
        var map = new HashMap<String, KPI>();
        map.put("test", conditionKpi);
        var assessment = factory.createAssessment(landscape, map);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }

    @Test
    void getAssessmentFromFactoryLandscape() {
        var foo = ItemFactory.getTestItem("a", "foo");
        var bar = ItemFactory.getTestItem("b", "bar");
        var landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();
        var assessment = factory.createAssessment(landscape);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }

    @Test
    void getAssessmentFromFactoryMap() {
        var fqi = FullyQualifiedIdentifier.build("test1", "test2", "test3");
        var statusList = new ArrayList<StatusValue>();
        var results = new HashMap<String, List<StatusValue>>();
        results.put(fqi.toString(), statusList);
        var assessment = factory.createAssessment(results);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }


    @Test
    void testNullValues() {
        var exception = assertThrows(NullPointerException.class, () -> factory.createAssessment(null, null));
        assertThat(exception.getMessage()).isEqualTo(ASSESSMENT_ERROR_NULL);
        exception = assertThrows(NullPointerException.class, () -> factory.createAssessment((Landscape) null));
        assertThat(exception.getMessage()).isEqualTo(ASSESSMENT_ERROR_NULL);
        exception = assertThrows(NullPointerException.class, () -> factory.createAssessment((Map<String, List<StatusValue>>) null));
        assertThat(exception.getMessage()).isEqualTo(ASSESSMENT_ERROR_NULL);
    }
}