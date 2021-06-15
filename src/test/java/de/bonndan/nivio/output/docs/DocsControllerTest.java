package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.assessment.AssessmentRepository;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.ExternalIcons;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocsControllerTest {

    public static DocsController docsController;
    private static AssessmentRepository assessmentRepository;
    private static LandscapeRepository landscapeRepository;

    @BeforeAll
    static void setup() {
        assessmentRepository = Mockito.mock(AssessmentRepository.class);
        landscapeRepository = Mockito.mock(LandscapeRepository.class);
        LocalServer localServer = new LocalServer("test");
        IconService iconService = new IconService(new LocalIcons(), new ExternalIcons(new HttpService()));
        docsController = new DocsController(landscapeRepository, localServer, iconService, assessmentRepository);
    }

    @Test
    void testConstructor() {
        assertThat(docsController.getClass()).isEqualTo(DocsController.class);
    }

    @Test
    void testNotFoundExceptionHtmlResource() {
        var exception = assertThrows(NotFoundException.class, () -> docsController.htmlResource("test1", null));
        assertThat(exception.getMessage()).isEqualTo("Landscape test1 not found");
    }

    @Test
    void testNotFoundExceptionOwners() {
        var exception = assertThrows(NotFoundException.class, () -> docsController.owners("test1", null));
        assertThat(exception.getMessage()).isEqualTo("Landscape test1 not found");
    }

    @Test
    void testHtmlResource() {
        var httpServletRequest = Mockito.mock(HttpServletRequest.class);
        var httpHeader = new HttpHeaders();
        httpHeader.add(HttpHeaders.CONTENT_TYPE, "text/html");
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(java.util.Optional.of(LandscapeFactory.createForTesting("test", "test").build()));
        Mockito.when(assessmentRepository.getAssessment(Mockito.any())).thenReturn(new Assessment(Map.of()));
        var response = docsController.htmlResource("test", httpServletRequest);
        assertThat(response).isEqualToIgnoringGivenFields(new ResponseEntity<>("", httpHeader, HttpStatus.OK), "body");
    }

    @Test
    void testOwners() {
        var httpServletRequest = Mockito.mock(HttpServletRequest.class);
        var httpHeader = new HttpHeaders();
        httpHeader.add(HttpHeaders.CONTENT_TYPE, "text/html");
        Mockito.when(landscapeRepository.findDistinctByIdentifier("test")).thenReturn(java.util.Optional.of(LandscapeFactory.createForTesting("test", "test").build()));
        Mockito.when(assessmentRepository.getAssessment(Mockito.any())).thenReturn(new Assessment(Map.of()));
        Mockito.when(httpServletRequest.getParameterMap()).thenReturn(Map.of());
        var response = docsController.owners("test", httpServletRequest);
        assertThat(response).isEqualToIgnoringGivenFields(new ResponseEntity<>("", httpHeader, HttpStatus.OK), "body");
    }
}
