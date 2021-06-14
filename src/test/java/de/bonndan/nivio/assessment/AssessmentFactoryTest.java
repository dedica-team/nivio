package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class AssessmentFactoryTest {


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
}