package de.bonndan.nivio.output.threeD;

import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class ThreeDRenderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreeDRenderController.class);

    private final LandscapeRepository landscapeRepository;
    private final String content;

    @Autowired
    public ThreeDRenderController(LandscapeRepository landscapeRepository) throws IOException {
        this.landscapeRepository = landscapeRepository;
        content = new String(Files.readAllBytes(new File("src/main/resources/static/html/3d.html").toPath()));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/render/{landscape}/threeD.html")
    public ResponseEntity<String> three3Resource(@PathVariable(name = "landscape") final String landscapeIdentifier) throws IOException {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(
                () -> new NotFoundException("Landscape " + landscapeIdentifier + " not found")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                content,
                headers,
                HttpStatus.OK
        );
    }
}
