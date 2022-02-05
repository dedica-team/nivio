package de.bonndan.nivio.assessment;

import de.bonndan.nivio.GraphTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssessmentRepositoryTest {


    private static AssessmentRepository assessmentRepository;
    private static GraphTestSupport graph;

    @BeforeAll
    static void setup() {
        graph = new GraphTestSupport();
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
        assessmentRepository.save(graph.landscape.getFullyQualifiedIdentifier(), assessment);

        //when
        var storedAssessment = assessmentRepository.getAssessment(graph.landscape.getFullyQualifiedIdentifier());

        //then
        assertThat(storedAssessment).isPresent().contains(assessment);
    }

    @Test
    void testGetNonExistingElement() {
        var storedAssessment = assessmentRepository.getAssessment(graph.landscape.getFullyQualifiedIdentifier());
        assertThat(storedAssessment).isNotPresent();
    }


    @Test
    void testIllegalArgumentExceptionGet() {
        var exception = assertThrows(NullPointerException.class, () -> assessmentRepository.getAssessment(null));
        assertThat(exception.getMessage()).isEqualTo("Null instead of FQI given");
    }

}
