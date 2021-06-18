package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.assessment.AssessmentRepository;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.IconService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(path = DocsController.PATH)
public class DocsController {

    public static final String PATH = "/docs";
    public static final String REPORT_HTML = "report.html";

    private final AssessmentRepository assessmentRepository;
    private final LandscapeRepository landscapeRepository;
    private final LocalServer localServer;
    private final IconService iconService;

    public DocsController(LandscapeRepository landscapeRepository, LocalServer localServer, IconService iconService, AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
        this.landscapeRepository = landscapeRepository;
        this.localServer = localServer;
        this.iconService = iconService;
    }

    private ResponseEntity<String> getResponseEntity(HttpServletRequest request, Landscape landscape, HtmlGenerator generator) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                generator.toDocument(landscape, assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier()).isPresent() ?
                                assessmentRepository.getAssessment(landscape.getFullyQualifiedIdentifier()).get() : assessmentRepository.createAssessment(landscape),
                        new SearchConfig(request.getParameterMap())),
                headers,
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/{landscape}/" + REPORT_HTML)
    public ResponseEntity<String> htmlResource(@PathVariable(name = "landscape") final String landscapeIdentifier, final HttpServletRequest request) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(
                () -> new NotFoundException("Landscape " + landscapeIdentifier + " not found")
        );

        ReportGenerator generator = new ReportGenerator(localServer, iconService);

        return getResponseEntity(request, landscape, generator);

    }

    @GetMapping(path = "/{landscape}/owners.html")
    public ResponseEntity<String> owners(@PathVariable(name = "landscape") final String landscapeIdentifier, final HttpServletRequest request) {

        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(
                () -> new NotFoundException("Landscape " + landscapeIdentifier + " not found")
        );

        OwnersReportGenerator generator = new OwnersReportGenerator(localServer, iconService);

        return getResponseEntity(request, landscape, generator);
    }
}
