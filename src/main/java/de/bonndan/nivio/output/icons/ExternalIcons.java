package de.bonndan.nivio.output.icons;

import de.bonndan.nivio.input.http.CachedResponse;
import de.bonndan.nivio.input.http.HttpService;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads images from external resources and converts them into data urls. Also provides shortcuts to common vendor icons.
 *
 *
 */
@Component
public class ExternalIcons {

    public static final String VENDOR_PREFIX = "vendor://";

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalIcons.class);

    private final Map<String, String> vendorIconUrls = new HashMap<>();
    private final Map<String, CachedResponse> imageCache = new ConcurrentHashMap<>();
    private final HttpService httpService;

    public ExternalIcons(HttpService httpService) {
        this.httpService = httpService;
    }

    @PostConstruct
    public void init() {
        vendorIconUrls.put("apache/httpd", "http://www.apache.org/logos/res/httpd/httpd.png");
        vendorIconUrls.put("redhat/keycloak", "https://raw.githubusercontent.com/keycloak/keycloak-misc/master/logo/keycloak_icon_256px.png");
        vendorIconUrls.put("k8s", "https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png");
        vendorIconUrls.put("kubernetes", "https://raw.githubusercontent.com/kubernetes/kubernetes/master/logo/logo.png");

        //redis.io/topics/trademark
        vendorIconUrls.put("redis", "https://redis.io/images/redis-white.png");
        vendorIconUrls.put("prometheus", "https://raw.githubusercontent.com/prometheus/docs/master/static/prometheus_logo.png");
    }

    /**
     * Returns the original url of a vendor product icon.
     *
     * @param vendor key in the config
     * @return data url or falls back to original url
     */
    public Optional<String> getUrl(String vendor) {

        if (StringUtils.isEmpty(vendor) || !vendorIconUrls.containsKey(vendor.toLowerCase())) {
            LOGGER.warn("Unknown vendor icon {}", vendor);
            return Optional.empty();
        }

        try {
            if (!imageCache.containsKey(vendor.toLowerCase())) {
                return load(vendor);
            }
            return responseToDataUrl(vendor, imageCache.get(vendor));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the original url of a vendor product icon.
     *
     * @param url key in the config
     * @return data url or falls back to original url
     */
    public Optional<String> getUrl(URL url) {

        if (url == null) {
            return Optional.empty();
        }

        try {
            String key = url.toString().toLowerCase();
            if (!imageCache.containsKey(key)) {
                return request(key, url);
            }
            return responseToDataUrl(key, imageCache.get(key));
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    private Optional<String> load(String vendor) {

        String iconRequestURI = vendorIconUrls.get(vendor);
        if (iconRequestURI == null) {
            return Optional.empty();
        }

        try {
            return request(vendor, new URL(iconRequestURI));
        } catch (MalformedURLException e) {
            LOGGER.warn("Failed to load vendor icon: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> request(String vendor, URL url) {

        CachedResponse cachedResponse;
        try {
            cachedResponse = httpService.getResponse(url);
        } catch (URISyntaxException | RuntimeException e) {
            LOGGER.warn("Failed to load vendor icon: {}", e.getMessage());
            return Optional.empty();
        }
        imageCache.put(vendor, cachedResponse);

        return responseToDataUrl(vendor, imageCache.get(vendor));
    }

    private Optional<String> responseToDataUrl(String vendor, CachedResponse cachedResponse) {

        Optional<String> contentType = getHeader(cachedResponse, "Content-type");
        LOGGER.debug("Downloaded file for vendor {} has content-type {}", vendor, contentType.orElse("unknown"));
        String dataUrl = getPrefix(contentType.orElse(""));
        dataUrl += DataUrlHelper.asBase64(cachedResponse.getBytes()).orElseThrow();
        return Optional.of(dataUrl);
    }

    private String getPrefix(String contentType) {
        if (contentType.contains("png"))
            return DataUrlHelper.DATA_IMAGE_PNG_XML_BASE_64;
        if (contentType.contains("jpeg"))
            return DataUrlHelper.DATA_IMAGE_JPEG_XML_BASE_64;
        if (contentType.contains("svg"))
            return DataUrlHelper.DATA_IMAGE_SVG_XML_BASE_64;

        return "";
    }

    private Optional<String> getHeader(CachedResponse cachedResponse, String headerName) {
        if (cachedResponse.getAllHeaders() == null) {
            return Optional.empty();
        }
        return Arrays.stream(cachedResponse.getAllHeaders())
                .filter(header -> header.getName().equalsIgnoreCase(headerName))
                .findFirst()
                .map(NameValuePair::getValue);
    }


    /**
     * Only used for unit testing.
     */
    public void add(String vendor, String url) {
        vendorIconUrls.put(vendor, url);
    }
}
