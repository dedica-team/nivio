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
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;


@Controller
@RequestMapping(path = "/render")
public class MapController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);
    public static final String MAP_JSON_ENDPOINT = "map.json";
    public static final String MAP_SVG_ENDPOINT = "map.svg";
    public static final String MAP_PNG_ENDPOINT = "graph.png";

    private final LandscapeRepository landscapeRepository;
    private final IconService iconService;
    private final MapFactory<mxGraph, mxCell> mapFactory;

    public MapController(LandscapeRepository landscapeRepository,
                         IconService iconService,
                         MapFactory<mxGraph, mxCell> mapFactory
    ) {
        this.landscapeRepository = landscapeRepository;
        this.iconService = iconService;
        this.mapFactory = mapFactory;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/" + MAP_JSON_ENDPOINT)
    public ResponseEntity<String> hex(
            @PathVariable(name = "landscape") final String landscapeIdentifier,
            @RequestParam(value = "size", required = false) Integer size
    ) throws IOException {
        LandscapeImpl landscape = getLandscape(landscapeIdentifier);

        ObjectMapper objectMapper = new ObjectMapper();
        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer(iconService);

        try {
            Rendered<mxGraph, mxCell> render = jGraphXRenderer.render(landscape);
            RenderedXYMap renderedMap = mapFactory.getRenderedMap(landscape, render);
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

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/" + MAP_SVG_ENDPOINT)
    public ResponseEntity<String> svg(@PathVariable(name = "landscape") final String landscapeIdentifier) {
        LandscapeImpl landscape = getLandscape(landscapeIdentifier);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml");
            return new ResponseEntity<>(
                    getMapAsString(landscape),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            LOGGER.warn("Could not render svg: ", ex);
            throw ex;
        }
    }


    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/" + MAP_PNG_ENDPOINT)
    public ResponseEntity<byte[]> pngResource(@PathVariable(name = "landscape") final String landscapeIdentifier) throws IOException {
        LandscapeImpl landscape = getLandscape(landscapeIdentifier);

        TranscoderInput input_svg_image = new TranscoderInput(new ByteArrayInputStream(getMapAsString(landscape).getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
        Transcoder transcoder = new PNGTranscoder();
        try {
            transcoder.transcode(input_svg_image, transcoderOutput);
        } catch (TranscoderException e) {
            throw new RuntimeException("Failed to create PNG", e);
        }
        outputStream.flush();
        outputStream.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
        return new ResponseEntity<>(
                outputStream.toByteArray(),
                headers,
                HttpStatus.OK
        );

    }

    private LandscapeImpl getLandscape(@PathVariable(name = "landscape") String landscapeIdentifier) {
        return landscapeRepository.findDistinctByIdentifier(landscapeIdentifier)
                .orElseThrow(() -> new NotFoundException("Landscape " + landscapeIdentifier + " not found"));
    }

    private String getMapAsString(LandscapeImpl landscape) {

        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer(iconService);
        Rendered<mxGraph, mxCell> render = jGraphXRenderer.render(landscape);
        RenderedXYMap renderedMap = mapFactory.getRenderedMap(landscape, render);

        SvgFactory svgFactory = new SvgFactory(renderedMap);
        return svgFactory.getXML();
    }
}