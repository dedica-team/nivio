package de.bonndan.nivio.assessment;

import de.bonndan.nivio.assessment.kpi.ConditionKPI;
import de.bonndan.nivio.assessment.kpi.KPI;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.bonndan.nivio.assessment.AssessmentFactory.errorMessageAssessmentNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AssessmentFactoryTest {


    @Test
    void getAssessmentFromFactoryLandscapeAndKPI() {
        var foo = ItemFactory.getTestItem("a", "foo");
        var bar = ItemFactory.getTestItem("b", "bar");
        var landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();
        var conditionKpi = new ConditionKPI();
        var map = new HashMap<String, KPI>();
        map.put("test", conditionKpi);
        var assessment = AssessmentFactory.createAssessment(landscape, map);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }

    @Test
    void getAssessmentFromFactoryLandscape() {
        var foo = ItemFactory.getTestItem("a", "foo");
        var bar = ItemFactory.getTestItem("b", "bar");
        var landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();
        var assessment = AssessmentFactory.createAssessment(landscape);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }

    @Test
    void getAssessmentFromFactoryMap() {
        var fqi = FullyQualifiedIdentifier.build("test1", "test2", "test3");
        var statusList = new ArrayList<StatusValue>();
        var results = new HashMap<FullyQualifiedIdentifier, List<StatusValue>>();
        results.put(fqi, statusList);
        var assessment = AssessmentFactory.createAssessment(results);
        assertThat(assessment.getClass()).isEqualTo(Assessment.class);
    }


    @Test
    void testNullValues() {
        var exception = assertThrows(NullPointerException.class, () -> AssessmentFactory.createAssessment(null, null));
        assertThat(exception.getMessage()).isEqualTo(errorMessageAssessmentNull);
        exception = assertThrows(NullPointerException.class, () -> AssessmentFactory.createAssessment((Landscape) null));
        assertThat(exception.getMessage()).isEqualTo(errorMessageAssessmentNull);
        exception = assertThrows(NullPointerException.class, () -> AssessmentFactory.createAssessment((Map<FullyQualifiedIdentifier, List<StatusValue>>) null));
        assertThat(exception.getMessage()).isEqualTo(errorMessageAssessmentNull);
    }
}