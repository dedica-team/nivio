package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class AssessmentRepositoryTest {

    private static Landscape landscape;

    private static AssessmentRepository assessmentRepository;

    @BeforeAll
    static void setup() {
        var foo = ItemFactory.getTestItem("a", "foo");
        var bar = ItemFactory.getTestItem("b", "bar");
        landscape = LandscapeFactory.createForTesting("test", "test").withItems(Set.of(foo, bar)).build();
        assessmentRepository = new AssessmentRepository();
    }

    @BeforeEach
    void cleanAssessmentRepository() {
        assessmentRepository.clean();
    }

    @Test
    void testGetExistingElement() {
        var assessment = assessmentRepository.createAssessment(landscape);
        var storedAssessment = assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier());
        assertThat(storedAssessment).isEqualTo(assessment);
    }

    @Test
    void testGetNonExistingElement() {
        var storedAssessment = assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier());
        assertThat(storedAssessment).isEqualTo(null);
    }

    @Test
    void testIllegalNullArguments() {
        var nullAssessment = assessmentRepository.createAssessment(null);
        assertThat(nullAssessment).isEqualTo(null);
        nullAssessment = assessmentRepository.getAssessment(null);
        assertThat(nullAssessment).isEqualTo(null);
    }

}
