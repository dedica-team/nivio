package de.bonndan.nivio.input;

import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.util.URLFactory;
import org.apache.http.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Fetches files either from local file system or from remote http endpoint.
 */
@Component
public class FileFetcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileFetcher.class);
    private static final String ERROR_MSG = "Failed to fetch file ";
    private final HttpService http;

    public FileFetcher(HttpService httpService) {
        this.http = httpService;
    }

    /**
     * @param source the file to read
     * @return the file content
     * @throws ReadingException if any error occurs
     */
    @NonNull
    public static String readFile(@NonNull final File source) {
        try {
            Objects.requireNonNull(source);
            return Files.readString(Paths.get(source.toURI()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Failed to read file {}", source.getAbsolutePath(), e);
            throw new ReadingException("Failed to read file " + source.getAbsolutePath(), e);
        }
    }

    /**
     * Reads from an {@link URL}
     *
     * @param url remote or local address to read
     * @return the content
     * @throws ReadingException on errors
     */
    public String get(URL url) {
        try {
            if (URLFactory.isLocal(url)) {
                return readFile(new File(url.toURI()));
            }
            return http.get(url);
        } catch (IOException | URISyntaxException | RuntimeException e) {
            LOGGER.error(ERROR_MSG + url, e);
            throw new ReadingException(ERROR_MSG + url, e);
        }
    }

    /**
     * Reads the url of a reference.
     * <p>
     * The given reference can also be a relative path. It then tries to concat the description source url.
     *
     * @param ref a {@link SourceReference} with a {@link de.bonndan.nivio.input.dto.LandscapeDescription}
     * @return the content or null
     */
    @Nullable
    public String get(SourceReference ref) {

        try {
            URL url = ref.getUrl();
            if (URLFactory.isLocal(url)) {
                return readFile(new File(url.toURI()));
            }
            return fetchRemoteUrl(ref);
        } catch (URISyntaxException e) {
            throw new ReadingException(String.format("Cannot get source reference content from %s", ref.getUrl()), e);
        }
    }

    /**
     * @param part    url or partial path
     * @param baseUrl optional base url
     * @return the file/url contents
     */
    @Nullable
    public String get(@NonNull final String part, @Nullable final URL baseUrl) {

        try {
            //we have no base url or source ref has absolute url
            if (baseUrl == null || Objects.requireNonNull(part).startsWith("http")) {
                return get(new URL(part));
            }

            //assemble new absolute url
            String combined = URLFactory.combine(baseUrl, part);
            URL url = new URL(combined);
            if (URLFactory.isLocal(url)) {
                return get(url);
            }
            return get(url);
        } catch (MalformedURLException e) {
            throw new ReadingException(String.format("Failed to build URL of %s with base url '%s'", part, baseUrl), e);
        }
    }

    private String fetchRemoteUrl(SourceReference ref) {
        try {
            return fetchRemoteUrl(ref, ref.getUrl());
        } catch (RuntimeException e) {
            LOGGER.error(ERROR_MSG + ref.getUrl(), e);
            throw new ReadingException(ERROR_MSG + ref.getUrl(), e);
        }
    }

    private String fetchRemoteUrl(SourceReference ref, URL url) {

        try {
            if (ref.hasBasicAuth()) {
                return http.getWithBasicAuth(url, ref.getBasicAuthUsername(), ref.getBasicAuthPassword());
            }
            if (ref.hasHeaderToken()) {
                return http.getWithHeaderToken(url, ref.getHeaderTokenName(), ref.getHeaderTokenValue());
            }
            return http.get(url);
        } catch (IOException | AuthenticationException | URISyntaxException | RuntimeException e) {
            LOGGER.error(ERROR_MSG + ref.getUrl(), e);
            throw new ReadingException(ERROR_MSG + ref.getUrl(), e);
        }
    }
}
