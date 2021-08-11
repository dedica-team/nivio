package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssessmentRepositoryTest {

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
    void testSaveAndGet() {
        //given
        var assessment = Assessment.empty();
        assessmentRepository.save(landscape.getFullyQualifiedIdentifier(), assessment);

        //when
        var storedAssessment = assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier());

        //then
        assertThat(storedAssessment).isPresent().contains(assessment);
    }

    @Test
    void testGetNonExistingElement() {
        var storedAssessment = assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier());
        assertThat(storedAssessment).isNotPresent();
    }


    @Test
    void testIllegalArgumentExceptionGet() {
        var exception = assertThrows(NullPointerException.class, () -> assessmentRepository.getAssessment(null));
        assertThat(exception.getMessage()).isEqualTo("Null instead of FQI given");
    }

}
