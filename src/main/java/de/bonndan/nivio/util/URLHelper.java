package de.bonndan.nivio.util;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class URLHelper {
    public static boolean isLocal(URL url) {
        return Objects.nonNull(url) && url.getProtocol().equals("file");
    }

    /**
     * Returns an URL is the string is an URL.
     * <p>
     * Tries to create an URL if a path is given.
     *
     * @param url string url or local path to file
     * @return an Optional of URL (empty on malformed urls)
     */
    public static Optional<URL> getURL(String url) {
        if (url == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            File file = new File(url);
            if (file.exists() && !url.startsWith("file:")) {
                try {
                    return Optional.of(file.toURI().toURL());
                } catch (MalformedURLException ignored) {
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the path of a file as URL or null.
     */
    public static Optional<URL> getParentPath(String url) {

        return getURL(url).map(u -> {
            try {
                return u.toURI().resolve(".").toURL();
            } catch (MalformedURLException | URISyntaxException e) {
                return null;
            }
        });
    }

    /**
     * Returns the combined base url and url part.
     * <p>
     * If one argument is null, the other is returned.
     *
     * @param baseUrl the base to be appended to
     * @param part    a relative path or full url/path or null
     * @return combined url as string or null if any of both is null
     */
    public static String combine(@Nullable URL baseUrl, @Nullable String part) {
        if (baseUrl == null && part == null) {
            return null;
        }
        if (baseUrl == null) {
            return part;
        }
        if (part == null) {
            return baseUrl.toString();
        }
        if (part.startsWith(baseUrl.toString()) || part.startsWith("http") || part.startsWith("file:/")) {
            return part;
        }

        String combined = baseUrl.toString().endsWith("/") ? baseUrl.toString() : baseUrl.toString() + "/";
        return combined + (part.startsWith("./") ? part.substring(2) : part);
    }

    /**
     * https://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection,
     */
    public static Map<String, String> splitQuery(@NonNull URL url) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String query = url.getQuery();
        if (query == null) {
            return queryPairs;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx == -1 || idx + 1 > pair.length() - 1) {
                continue;
            }
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return queryPairs;
    }
}
