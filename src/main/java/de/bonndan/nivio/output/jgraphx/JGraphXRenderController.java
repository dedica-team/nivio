package de.bonndan.nivio.output.jgraphx;

import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.IconService;
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
import java.util.Optional;


@Controller
@RequestMapping(path = "/render")
public class JGraphXRenderController {

    private static final Logger logger = LoggerFactory.getLogger(de.bonndan.nivio.output.jgraphx.JGraphXRenderController.class);

    private final LandscapeRepository landscapeRepository;
    private final IconService iconService;

    @Autowired
    public JGraphXRenderController(LandscapeRepository landscapeRepository, IconService iconService) {
        this.landscapeRepository = landscapeRepository;
        this.iconService = iconService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/graph.png")
    public ResponseEntity<byte[]> pngResource(@PathVariable(name = "landscape") final String landscapeIdentifier) throws IOException {
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(() ->
             new NotFoundException("Not found: " + landscapeIdentifier)
        );

        JGraphXRenderer graphStreamRenderer = new JGraphXRenderer(iconService);
        File png = File.createTempFile(landscapeIdentifier.replace(":", "_"), "png");
        try {
            graphStreamRenderer.render(landscape, png);
        } catch (Exception ex) {
            logger.warn("Could not render graph: " );
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