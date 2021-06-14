package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.LandscapeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class AssessmentControllerTest {

    private static AssessmentController assessmentController;
    private static AssessmentRepository assessmentRepository;

    @BeforeAll
    static void setup() {
        assessmentRepository = Mockito.mock(AssessmentRepository.class);
        assessmentController = new AssessmentController(new LandscapeRepository(), assessmentRepository);
    }

    @BeforeEach
    void cleanAssessmentRepository() {
        assessmentRepository.clean();
    }

    @Test
    void testConstructor() {
        assertThat(assessmentController.getClass()).isEqualTo(AssessmentController.class);
    }

}
