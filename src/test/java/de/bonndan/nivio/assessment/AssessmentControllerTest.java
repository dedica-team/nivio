package de.bonndan.nivio.assessment;

import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentControllerTest {

    private static AssessmentController assessmentController;
    private static AssessmentRepository assessmentRepository;
    private static LandscapeRepository landscapeRepository;

    @BeforeAll
    static void setup() {
        assessmentRepository = Mockito.mock(AssessmentRepository.class);
        landscapeRepository = new LandscapeRepository();
        assessmentController = new AssessmentController(landscapeRepository, assessmentRepository);
    }

    @BeforeEach
    void cleanAssessmentRepository() {
        assessmentRepository.clean();
    }

    @Test
    void testConstructor() {
        assertThat(assessmentController.getClass()).isEqualTo(AssessmentController.class);
    }

    @Test
    void assessmentFoundInRepository() {
        var assessment = new Assessment(Map.of());
        var landscape = LandscapeFactory.createForTesting("test", "test").build();
        Mockito.when(assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier())).thenReturn(assessment);
        landscapeRepository.save(landscape);
        var responseEntity = assessmentController.landscape("test");
        assertThat(responseEntity).isEqualTo(new ResponseEntity<>(assessment, HttpStatus.OK));
        Mockito.when(assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier())).thenReturn(null);
        assertThat(responseEntity).isEqualTo(new ResponseEntity<>(assessment, HttpStatus.OK));
    }
}
