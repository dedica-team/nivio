package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.IconService;
import de.bonndan.nivio.output.jgraphx.FinalGraph;
import de.bonndan.nivio.output.jgraphx.JGraphXRenderer;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


@Controller
@RequestMapping(path = "/render")
public class JsonRenderController {

    private static final Logger logger = LoggerFactory.getLogger(JsonRenderController.class);

    private final LandscapeRepository landscapeRepository;
    private final IconService iconService;

    @Autowired
    public JsonRenderController(LandscapeRepository landscapeRepository, IconService iconService) {
        this.landscapeRepository = landscapeRepository;
        this.iconService = iconService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/hex.json")
    public ResponseEntity<String> hex(
            @PathVariable(name = "landscape") final String landscapeIdentifier,
            @RequestParam(value = "size", required = false) Integer size
    ) throws IOException {
        Optional<LandscapeImpl> landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier);
        if (!landscape.isPresent())
            throw new NotFoundException("Not found");

        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer(iconService);

        try {
            FinalGraph graph = new FinalGraph(iconService);
            jGraphXRenderer.getMxGraph(landscape.get(), graph);
            RenderedMap from = RenderedMap.from(graph);
            MapItem[] items = from.items.toArray(MapItem[]::new);
            from.items.clear();
            Arrays.stream(items).forEach((i -> from.items.add(new HexMapItem((XYMapItem) i, size == null ? 100 : size))));

            HttpHeaders headers = new HttpHeaders();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            return new ResponseEntity<>(
                    objectMapper.writeValueAsString(from),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            logger.warn("Could not render graph: " );
            throw ex;
        }
    }

    /**
     * Prints the landscape as json based on rendering
     *
     *
     */
    //TODO todo provide officially supported 3d format like https://threejs.org/docs/#examples/loaders/OBJLoader
    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/threejs.json")
    public ResponseEntity<String> json(@PathVariable(name = "landscape") final String landscapeIdentifier) throws IOException {
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(() ->
                new NotFoundException("Not found: " + landscapeIdentifier)
        );

        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer(iconService);
        JsonRenderer renderer = new JsonRenderer(jGraphXRenderer);

        try {
            String rendered = renderer.render(landscape);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            return new ResponseEntity<>(
                    rendered,
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            logger.warn("Could not render graph: " );
            throw ex;
        }
    }
}