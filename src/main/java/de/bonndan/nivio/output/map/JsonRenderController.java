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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


@Controller
@RequestMapping(path = "/render")
public class JsonRenderController {

    private static final Logger logger = LoggerFactory.getLogger(JsonRenderController.class);

    private final LandscapeRepository landscapeRepository;
    private final IconService iconService;

    public JsonRenderController(LandscapeRepository landscapeRepository, IconService iconService) {
        this.landscapeRepository = landscapeRepository;
        this.iconService = iconService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/hex.json")
    public ResponseEntity<String> hex(
            JGraphXRenderer jGraphXRenderer,
            @PathVariable(name = "landscape") final String landscapeIdentifier,
            @RequestParam(value = "size", required = false) Integer size
    ) throws IOException {
        Optional<LandscapeImpl> landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier);
        if (landscape.isEmpty()) {
            throw new NotFoundException("Not found");
        }


        try {

            Rendered<mxGraph, mxCell> render = jGraphXRenderer.render(landscape.get());
            RenderedXYMap xyMap = getRenderedMap(render);
            MapItem[] items = xyMap.items.toArray(MapItem[]::new);
            xyMap.items.clear();
            Arrays.stream(items).forEach((i -> xyMap.items.add(new HexMapItem((XYMapItem) i, size == null ? 100 : size))));

            HttpHeaders headers = new HttpHeaders();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
            return new ResponseEntity<>(
                    objectMapper.writeValueAsString(getRenderedHexMap(xyMap)),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            logger.warn("Could not render graph: " );
            throw ex;
        }
    }

    private RenderedXYMap getRenderedMap(Rendered<mxGraph, mxCell> graph) {
        RenderedXYMap renderedMap = RenderedXYMap.from(graph);

        AtomicInteger minX = new AtomicInteger(0);
        AtomicInteger maxX = new AtomicInteger(0);
        AtomicInteger minY = new AtomicInteger(0);
        AtomicInteger maxY =  new AtomicInteger(0);
        renderedMap.items.forEach(item -> {
            if (item.x < minX.get())
                minX.set((int) item.x);
            if (item.x > maxX.get())
                maxX.set((int) item.x);
            if (item.y < minY.get())
                minY.set((int) item.y);
            if (item.y > maxY.get())
                maxY.set((int) item.y);
        });

        renderedMap.width = maxX.get() - minX.get();
        renderedMap.height = maxY.get() - minY.get();

        return renderedMap;
    }

    @NotNull
    private RenderedHexMap getRenderedHexMap(RenderedXYMap renderedMap) {
        MapItem[] items = renderedMap.items.toArray(MapItem[]::new);
        int size = Math.max(renderedMap.width, renderedMap.height) / 40;

        List<HexMapItem> hexmapItems = new ArrayList<>();
        RenderedHexMap renderedHexMap = new RenderedHexMap();
        Arrays.stream(items).forEach((i -> {
            HexMapItem hmi = new HexMapItem((XYMapItem) i, size);
            hexmapItems.add(hmi);
        }));
        renderedHexMap.items.addAll(hexmapItems);

        AtomicInteger minQ = new AtomicInteger(0);
        AtomicInteger maxQ = new AtomicInteger(0);
        AtomicInteger minR = new AtomicInteger(0);
        AtomicInteger maxR =  new AtomicInteger(0);
        hexmapItems.forEach(xyMapItem -> {
            Hex hex = xyMapItem.getHex();
            if (hex.q < minQ.get())
                minQ.set(hex.q);
            if (hex.q > maxQ.get())
                maxQ.set(hex.q);
            if (hex.r < minR.get())
                minR.set(hex.r);
            if (hex.r > maxR.get())
                maxR.set(hex.r);
        });

        renderedHexMap.minQ = minQ.get();
        renderedHexMap.maxQ = maxQ.get();
        renderedHexMap.minR = minR.get();
        renderedHexMap.maxR = maxR.get();

        return renderedHexMap;
    }

    /**
     * Prints the landscape as json based on rendering
     *
     *
     */
    //TODO todo provide officially supported 3d format like https://threejs.org/docs/#examples/loaders/OBJLoader
    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/threejs.json")
    public ResponseEntity<String> json(
            JGraphXRenderer jGraphXRenderer,
            @PathVariable(name = "landscape") final String landscapeIdentifier) {
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier).orElseThrow(() ->
                new NotFoundException("Not found: " + landscapeIdentifier)
        );

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