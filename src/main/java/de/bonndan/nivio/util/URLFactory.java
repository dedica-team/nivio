package de.bonndan.nivio.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * This factory supports the conversion of strings into urls.
 *
 *
 */
public class URLFactory {

    static final String RELATIVE_PATH_PLACEHOLDER = "file://_relative_path_/";

    private static final Logger LOGGER = LoggerFactory.getLogger(URLFactory.class);

    private URLFactory() {
    }

    /**
     * Checks if the url points to a file.
     *
     * @param url url to check
     * @return true if the url protocol is "file"
     */
    public static boolean isLocal(@Nullable final URL url) {
        return Objects.nonNull(url) && url.getProtocol().equals("file");
    }

    /**
     * Returns an URL is the string is an URL.
     *
     * Tries to create a URL if a path to a file is given, but the file must exist.
     *
     * @param url may contain links, files and relative paths
     * @return an Optional of URL (empty on malformed urls)
     */
    public static Optional<URL> getURL(@Nullable final String url) {
        if (!StringUtils.hasLength(url)) {
            return Optional.empty();
        }

        try {
            return Optional.of(new URL(url));
        } catch (MalformedURLException e) {
            File file = new File(url);
            if (file.exists() && !url.startsWith("file:")) {
                try {
                    URL recoveredFromFile = file.toURI().toURL();
                    LOGGER.debug("Created a URL for {}", recoveredFromFile);
                    return Optional.of(recoveredFromFile);
                } catch (MalformedURLException ignored) {
                    LOGGER.warn("Failed to create a URL from {}", url);
                }
            }

            if (url.startsWith(".")) {
                try {
                    URL relative = new URL(combine(new URL(RELATIVE_PATH_PLACEHOLDER), url));
                    LOGGER.debug("Created URL for relative path {}", url);
                    return Optional.of(relative);
                } catch (MalformedURLException ex) {
                    LOGGER.warn("Could not construct relative url using {}", url);
                }
            }
            if (LOGGER.isDebugEnabled()) LOGGER.debug("Malformed URL: {}", url);
        }
        return Optional.empty();
    }

    /**
     * Returns the path of a file as URL or null.
     */
    public static Optional<URL> getParentPath(@NonNull final URL url) {
        try {
            Objects.requireNonNull(url);
            return Optional.of(url.toURI().resolve(".").toURL());
        } catch (MalformedURLException | URISyntaxException e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the combined base url and url part.
     *
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

        String combined = baseUrl.toString().endsWith("/") ? baseUrl.toString() : baseUrl + "/";
        return combined + (part.startsWith("./") ? part.substring(2) : part);
    }


    public static Optional<String> getOriginalRelativePath(@NonNull final URL url) {
        Objects.requireNonNull(url);

        if (url.toString().startsWith(RELATIVE_PATH_PLACEHOLDER)) {
            String originalUrl = url.toString().replace(URLFactory.RELATIVE_PATH_PLACEHOLDER, "./");
            return Optional.of(originalUrl);
        }

        return Optional.empty();
    }

    /**
     * Deserializer to turn strings into URL instances.
     *
     *
     */
    public static class URLDeserializer extends JsonDeserializer<URL> {

        @Override
        public URL deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectCodec objectCodec = p.getCodec();
            JsonNode node = objectCodec.readTree(p);
            String stringUrl = node.asText();
            return getURL(stringUrl).orElse(null);
        }
    }
}
