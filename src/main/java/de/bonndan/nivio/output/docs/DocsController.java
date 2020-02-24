package de.bonndan.nivio.output.docs;

import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.LocalServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequestMapping(path = "/docs")
public class DocsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocsController.class);

    private final LandscapeRepository landscapeRepository;
    private final LocalServer localServer;

    public DocsController(LandscapeRepository landscapeRepository, LocalServer localServer) {
        this.landscapeRepository = landscapeRepository;
        this.localServer = localServer;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/report.html")
    public ResponseEntity<String> htmlResource(@PathVariable(name = "landscape") final String landscapeIdentifier) {

        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(
                () -> new NotFoundException("Landscape " + landscapeIdentifier + " not found")
        );

        ReportGenerator generator = new ReportGenerator(localServer);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                generator.toDocument(landscape),
                headers,
                HttpStatus.OK
        );

    }

    /**
     * Renders only a single item as html.
     *
     */
    @RequestMapping(method = RequestMethod.GET, path = "/item/**")
    public ResponseEntity<String> itemHtmlResource(HttpServletRequest request) {

        String requestURL = request.getRequestURL().toString();
        String fqiString = requestURL.split("/item/")[1];
        FullyQualifiedIdentifier fqi;
        try {
            fqi = FullyQualifiedIdentifier.from(fqiString);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(fqi.getLandscape()).orElseThrow(
                () -> new NotFoundException("Landscape " + fqi.getLandscape() + " not found")
        );
        Optional<Item> item = landscape.getItems().find(fqi);
        if (item.isEmpty()) {
            LOGGER.warn("Could not find item {}", fqiString);
            return ResponseEntity.notFound().build();
        }

        ItemReportGenerator generator = new ItemReportGenerator(localServer);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                generator.toDocument(item.get()),
                headers,
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/owners.html")
    public ResponseEntity<String> owners(@PathVariable(name = "landscape") final String landscapeIdentifier) {

        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(
                () -> new NotFoundException("Landscape " + landscapeIdentifier + " not found")
        );

        OwnersReportGenerator generator = new OwnersReportGenerator(localServer);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                generator.toDocument(landscape),
                headers,
                HttpStatus.OK
        );

    }
}
