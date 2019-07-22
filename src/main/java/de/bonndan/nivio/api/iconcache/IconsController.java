package de.bonndan.nivio.api.iconcache;

import de.bonndan.nivio.input.http.CachedResponse;
import de.bonndan.nivio.input.http.HttpService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * http://localhost:8080/icons//https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png
 */
@Controller
@RequestMapping(path = "/icons")
public class IconsController {

    private final Map<String, CachedResponse> imageCache;

    private final HttpService httpService;

    public IconsController(Map<String, CachedResponse> imageCache, HttpService httpService) {
        this.imageCache = imageCache;
        this.httpService = httpService;
    }

    @RequestMapping(path = "/**", method = RequestMethod.GET)
    public ResponseEntity<byte[]> icons(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        String part = "/icons/";
        int i = requestURI.indexOf(part);
        String iconRequestURI = requestURI.substring(i + part.length());
        iconRequestURI = StringUtils.trimLeadingCharacter(iconRequestURI, '/');
        if (imageCache.containsKey(iconRequestURI)) {
            sendResponse(imageCache.get(iconRequestURI));
        }

        try {
            CachedResponse cachedResponse = httpService.getResponse(new URL(iconRequestURI));
            imageCache.put(iconRequestURI, cachedResponse);
            return sendResponse(cachedResponse);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
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
