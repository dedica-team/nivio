package de.bonndan.nivio.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
}
