package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.util.URLHelper;
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
     * @param source
     * @return the file content
     * @throws ReadingException
     */
    @NonNull
    public static String readFile(File source) {
        try {
            return Files.readString(Paths.get(source.toURI()), StandardCharsets.UTF_8);
        } catch (IOException | OutOfMemoryError | SecurityException e) {
            LOGGER.error("Failed to read file " + source.getAbsolutePath(), e);
            throw new ReadingException("Failed to read file " + source.getAbsolutePath(), e);
        }
    }

    /**
     * Reads a local file.
     *
     * @param file the file to read
     * @return the file content
     * @throws ReadingException
     */
    public String get(File file) {
        return readFile(file);
    }

    /**
     * Reads from an {@link URL}
     *
     * @param url remote or local address to read
     * @return the content
     * @throws ReadingException
     */
    public String get(URL url) {
        try {
            if (URLHelper.isLocal(url)) {
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
            URL url = new URL(ref.getUrl());
            url.toURI(); //to force exception early
            if (URLHelper.isLocal(url)) {
                return readFile(new File(url.toURI()));
            }
            return fetchRemoteUrl(ref);
        } catch (MalformedURLException | URISyntaxException e) {
            String path = ref.getUrl();
            if (path == null) {
                return null;
            }
            if (ref.getLandscapeDescription() != null) {
                File file = new File(ref.getLandscapeDescription().getSource());
                path = file.getParent() + "/" + ref.getUrl();
            }
            File source = new File(path);
            return readFile(source);
        }
    }

    /**
     * @param ref
     * @param baseUrl
     * @return
     */
    public String get(SourceReference ref, URL baseUrl) {

        //we have no base url or source ref has absolute url
        if (baseUrl == null || ref.getUrl().startsWith("http")) {
            return get(ref);
        }

        if (ref.getUrl() == null) {
            return null;
        }

        //assemble new absolute url
        String combined = URLHelper.combine(baseUrl, ref.getUrl());
        try {
            URL url = new URL(combined);
            if (URLHelper.isLocal(url)) {
                return get(url);
            }
            return fetchRemoteUrl(ref, url);
        } catch (MalformedURLException e) {
            throw new ReadingException("Failed to build URL of " + combined, e);
        }
    }

    private String fetchRemoteUrl(SourceReference ref) {
        try {
            return fetchRemoteUrl(ref, new URL(ref.getUrl()));
        } catch (IOException | RuntimeException e) {
            LOGGER.error(ERROR_MSG + ref.getUrl(), e);
            throw new ReadingException(ref.getLandscapeDescription(), ERROR_MSG + ref.getUrl(), e);
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
            throw new ReadingException(ref.getLandscapeDescription(), ERROR_MSG + ref.getUrl(), e);
        }
    }
}
