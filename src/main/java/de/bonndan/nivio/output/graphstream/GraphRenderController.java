package de.bonndan.nivio.output.graphstream;

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
@RequestMapping(path = "/renderg")
public class GraphRenderController {

    private static final Logger logger = LoggerFactory.getLogger(GraphRenderController.class);

    private final LandscapeRepository landscapeRepository;

    @Autowired
    public GraphRenderController(LandscapeRepository landscapeRepository) {
        this.landscapeRepository = landscapeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/graph.png")
    public ResponseEntity<byte[]> pngResource(@PathVariable(name = "landscape") final String landscapeIdentifier) throws IOException {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(
                () -> new NotFoundException("Landscape " + landscapeIdentifier + " not found")
        );

        GraphStreamRenderer graphStreamRenderer = new GraphStreamRenderer();
        File png = File.createTempFile(landscapeIdentifier, "png");
        try {
            graphStreamRenderer.render(landscape, png);
        } catch (Exception ex) {
            logger.warn("Could not render graph: " + graphStreamRenderer.getGraphDump());
            throw ex;
        }

        byte[] bFile = Files.readAllBytes(png.toPath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
        return new ResponseEntity<>(
                bFile,
                headers,
                HttpStatus.OK
        );

    }


}
