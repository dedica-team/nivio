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
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class ThreeDRenderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreeDRenderController.class);

    private final LandscapeRepository landscapeRepository;
    private final String content;

    @Autowired
    public ThreeDRenderController(LandscapeRepository landscapeRepository) throws IOException {
        this.landscapeRepository = landscapeRepository;
        InputStream resourceAsStream = getClass().getResourceAsStream("/static/html/3d.html");
        content = new String(StreamUtils.copyToByteArray(resourceAsStream));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/render/{landscape}/threeD.html")
    public ResponseEntity<String> three3Resource(@PathVariable(name = "landscape") final String landscapeIdentifier) {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier);
        if (landscape == null)
            throw new NotFoundException("Not found");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                content,
                headers,
                HttpStatus.OK
        );
    }
}
