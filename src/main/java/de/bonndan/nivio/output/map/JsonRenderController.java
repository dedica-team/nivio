package de.bonndan.nivio.output.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.IconService;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.output.jgraphx.JGraphXRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;


@Controller
@RequestMapping(path = "/render")
public class JsonRenderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonRenderController.class);

    private final LandscapeRepository landscapeRepository;
    private final IconService iconService;
    private final MapFactory<mxGraph, mxCell> mapFactory;

    public JsonRenderController(LandscapeRepository landscapeRepository,
                                IconService iconService,
                                MapFactory<mxGraph, mxCell> mapFactory
    ) {
        this.landscapeRepository = landscapeRepository;
        this.iconService = iconService;
        this.mapFactory = mapFactory;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/map.json")
    public ResponseEntity<String> hex(
            @PathVariable(name = "landscape") final String landscapeIdentifier,
            @RequestParam(value = "size", required = false) Integer size
    ) throws IOException {
        Optional<LandscapeImpl> landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier);
        if (landscape.isEmpty()) {
            throw new NotFoundException("Not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer(iconService);

        try {
            Rendered<mxGraph, mxCell> render = jGraphXRenderer.render(landscape.get());
            RenderedXYMap renderedMap = mapFactory.getRenderedMap(landscape.get(), render);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            return new ResponseEntity<>(
                    objectMapper.writeValueAsString(renderedMap),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            LOGGER.warn("Could not render graph: ", ex);
            throw ex;
        }
    }
}