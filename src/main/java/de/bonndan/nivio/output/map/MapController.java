package de.bonndan.nivio.output.map;

import de.bonndan.nivio.api.NotFoundException;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping(path = MapController.PATH)
public class MapController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapController.class);
    public static final String MAP_SVG_ENDPOINT = "map.svg";
    public static final String MAP_PNG_ENDPOINT = "graph.png";
    public static final String PATH = "/render";

    private final LandscapeRepository landscapeRepository;
    private final PNGRenderCache pngRenderCache;

    public MapController(LandscapeRepository landscapeRepository, PNGRenderCache pngRenderCache) {
        this.landscapeRepository = landscapeRepository;
        this.pngRenderCache = pngRenderCache;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/" + MAP_SVG_ENDPOINT)
    public ResponseEntity<String> svg(@PathVariable(name = "landscape") final String landscapeIdentifier) {
        LandscapeImpl landscape = getLandscape(landscapeIdentifier);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml");
            return new ResponseEntity<>(
                    pngRenderCache.getSVG(landscape),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            LOGGER.warn("Could not render svg: ", ex);
            throw ex;
        }
    }


    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/" + MAP_PNG_ENDPOINT)
    public ResponseEntity<byte[]> pngResource(@PathVariable(name = "landscape") final String landscapeIdentifier) {
        LandscapeImpl landscape = getLandscape(landscapeIdentifier);
        byte[] png = pngRenderCache.getPNG(landscape);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
        return new ResponseEntity<>(png, headers, HttpStatus.OK);
    }

    private LandscapeImpl getLandscape(@PathVariable(name = "landscape") String landscapeIdentifier) {
        return landscapeRepository.findDistinctByIdentifier(landscapeIdentifier)
                .orElseThrow(() -> new NotFoundException("Landscape " + landscapeIdentifier + " not found"));
    }
}