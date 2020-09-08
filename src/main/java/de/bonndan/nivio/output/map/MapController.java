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
    public static final String PATH = "/render";

    private final LandscapeRepository landscapeRepository;
    private final RenderCache renderCache;

    public MapController(LandscapeRepository landscapeRepository, RenderCache renderCache) {
        this.landscapeRepository = landscapeRepository;
        this.renderCache = renderCache;
    }

    @CrossOrigin(methods = RequestMethod.GET)
    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}/" + MAP_SVG_ENDPOINT)
    public ResponseEntity<String> svg(@PathVariable(name = "landscape") final String landscapeIdentifier) {
        LandscapeImpl landscape = getLandscape(landscapeIdentifier);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/svg+xml");
            return new ResponseEntity<>(
                    renderCache.getSVG(landscape),
                    headers,
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            LOGGER.warn("Could not render svg: ", ex);
            throw ex;
        }
    }

    private LandscapeImpl getLandscape(@PathVariable(name = "landscape") String landscapeIdentifier) {
        return landscapeRepository.findDistinctByIdentifier(landscapeIdentifier)
                .orElseThrow(() -> new NotFoundException("Landscape " + landscapeIdentifier + " not found"));
    }
}