package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.input.http.CachedResponse;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.output.LocalServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import static de.bonndan.nivio.output.LocalServer.VENDORICONS_PATH;

/**
 * http://localhost:8080/vendoricons//https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png
 */
@Controller
@RequestMapping(path = VENDORICONS_PATH)
public class IconsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IconsController.class);


    private final Map<String, CachedResponse> imageCache;

    private final HttpService httpService;

    public IconsController(Map<String, CachedResponse> imageCache, HttpService httpService) {
        this.imageCache = imageCache;
        this.httpService = httpService;
    }

    @RequestMapping(path = "/**", method = RequestMethod.GET)
    public ResponseEntity<byte[]> icons(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        String iconRequestURI;
        try {
            iconRequestURI = LocalServer.deproxyUrl(requestURI);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Failed to decode " + requestURI + " : " + ex.getMessage());
            return ResponseEntity.badRequest().build();
        }

        if (imageCache.containsKey(iconRequestURI)) {
            return sendResponse(imageCache.get(iconRequestURI));
        }

        try {
            CachedResponse cachedResponse = httpService.getResponse(new URL(iconRequestURI));
            imageCache.put(iconRequestURI, cachedResponse);
            return sendResponse(cachedResponse);
        } catch (IOException | URISyntaxException | RuntimeException e) {
            LOGGER.warn("Failed to load icon: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<byte[]> sendResponse(CachedResponse cachedResponse) {
        HttpHeaders headers = new HttpHeaders();
        Arrays.stream(cachedResponse.getAllHeaders()).forEach(header -> headers.add(header.getName(), header.getValue()));
        return new ResponseEntity<>(
                cachedResponse.getBytes(),
                headers,
                HttpStatus.OK
        );
    }
}
