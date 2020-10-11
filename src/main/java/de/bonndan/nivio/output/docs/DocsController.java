package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.assessment.kpi.KPIFactory;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.LocalIcons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(path = DocsController.PATH)
public class DocsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocsController.class);
    public static final String PATH = "/docs";
    public static final String REPORT_HTML = "report.html";

    private final LandscapeRepository landscapeRepository;
    private final LocalServer localServer;
    private final LocalIcons localIcons;
    private final KPIFactory factory;

    public DocsController(LandscapeRepository landscapeRepository, LocalServer localServer, LocalIcons localIcons, KPIFactory factory) {
        this.landscapeRepository = landscapeRepository;
        this.localServer = localServer;
        this.localIcons = localIcons;
        this.factory = factory;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/" + REPORT_HTML)
    public ResponseEntity<String> htmlResource(@PathVariable(name = "landscape") final String landscapeIdentifier) {

        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(
                () -> new NotFoundException("Landscape " + landscapeIdentifier + " not found")
        );

        ReportGenerator generator = new ReportGenerator(localServer, localIcons, factory);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                generator.toDocument(landscape),
                headers,
                HttpStatus.OK
        );

    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/owners.html")
    public ResponseEntity<String> owners(@PathVariable(name = "landscape") final String landscapeIdentifier) {

        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(
                () -> new NotFoundException("Landscape " + landscapeIdentifier + " not found")
        );

        OwnersReportGenerator generator = new OwnersReportGenerator(localServer, localIcons);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                generator.toDocument(landscape),
                headers,
                HttpStatus.OK
        );

    }
}
