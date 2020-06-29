package de.bonndan.nivio.util;

import org.springframework.lang.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class URLHelper {

    public static boolean isLocal(URL url) {
        return Objects.nonNull(url) && url.toString().startsWith("file:/");
    }

    /**
     * Returns an URL is the string is an URL.
     *
     * Tries to create an URL if a path is given.
     *
     * @param url string url or local path to file
     * @return an URL or null
     */
    @Nullable
    public static URL getURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            File file = new File(url);
            if (file.exists() && !url.startsWith("file:")) {
                return getURL("file:" + url);
            }
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

    /**
     * Returns the combined base url and url part.
     *
     * If one argument is null, the other is returned.
     *
     * @param baseUrl
     * @param part a relative path or full url/path or null
     * @return combined url as string
     */
    public static String combine(@Nullable URL baseUrl, @Nullable String part) {
        if (baseUrl == null) {
            return part;
        }

        if (part == null) {
            return baseUrl.toString();
        }

        if (part.startsWith(baseUrl.toString())) {
            return part;
        }

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
