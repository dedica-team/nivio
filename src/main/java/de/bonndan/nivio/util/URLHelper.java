package de.bonndan.nivio.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class URLHelper {

    public static boolean isLocal(URL url) {
        return Objects.nonNull(url) && url.toString().startsWith("file:/");
    }

    public static URL getURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Returns the path of a file as URL or null
     *
     */
    public static URL getParentPath(String url) {
        URL url1 = getURL(url);
        if (url1 == null)
            return null;
        try {
            return url1.toURI().resolve(".").toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            return null;
        }
    }

    public static String combine(URL baseUrl, String part) {
        if (baseUrl == null)
            return part;
        if (part == null)
            return baseUrl.toString();

        String combined = baseUrl.toString().endsWith("/") ? baseUrl.toString() : baseUrl.toString() + "/";
        return combined + (part.startsWith("./") ? part.substring(2) : part);
    }

    /**
     * https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection,
     */
    public static Map<String, String> splitQuery(URL url) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return query_pairs;
    }
}
