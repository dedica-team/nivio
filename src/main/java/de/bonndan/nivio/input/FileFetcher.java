package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.util.URLHelper;
import org.apache.http.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Fetches files either from local file system or from remote http endpoint.
 */
@Component
public class FileFetcher {

    private static Logger logger = LoggerFactory.getLogger(FileFetcher.class);

    private final HttpService http;

    @Autowired
    public FileFetcher(HttpService httpService) {
        this.http = httpService;
    }

    public String get(URL url) {
        try {
            return http.get(url);
        } catch (IOException | URISyntaxException | RuntimeException e) {
            logger.error("Failed to fetch file " + url, e);
            throw new ReadingException("Failed to fetch file "+ url, e);
        }
    }

    public String get(SourceReference ref) {

        try {
            URL url = new URL(ref.getUrl());
            url.toURI(); //to force exception early
            return fetchUrl(ref);
        } catch (MalformedURLException | URISyntaxException e) {
            String path = ref.getUrl();
            if (path == null)
                return null;
            if (ref.getEnvironment() != null) {
                File file = new File(ref.getEnvironment().getSource());
                path = file.getParent() + "/" + ref.getUrl();
            }
            File source = new File(path);
            return readFile(source);
        }
    }

    public String get(SourceReference ref, URL baseUrl) {

        //we have no base url or source ref has absolute url
        if (baseUrl == null || ref.getUrl().startsWith("http")) {
            return get(ref);
        }

        if (ref.getUrl() == null)
            return null;
        //assemble new absolute url
        ref.setUrl(URLHelper.combine(baseUrl, ref.getUrl()));
        return get(ref);
    }

    private String fetchUrl(SourceReference ref) {

        try {
            if (ref.hasBasicAuth()) {
                return http.getWithBasicAuth(
                        new URL(ref.getUrl()),
                        ref.getBasicAuthUsername(), ref.getBasicAuthPassword()
                );
            }
            if (ref.hasHeaderToken()) {
                return http.getWithHeaderToken(
                        new URL(ref.getUrl()),
                        ref.getHeaderTokenName(), ref.getHeaderTokenValue()
                );
            }
            return http.get(new URL(ref.getUrl()));
        } catch (IOException | AuthenticationException | URISyntaxException | RuntimeException e) {
            logger.error("Failed to fetch file " + ref.getUrl(), e);
            throw new ReadingException(ref.getEnvironment(), "Failed to fetch file "+ ref.getUrl(), e);
        }
    }

    public static String readFile(File source) {
        try {
            return new String(Files.readAllBytes(Paths.get(source.toURI())));
        } catch (IOException e) {
            logger.error("Failed to read file " + source.getAbsolutePath(), e);
            throw new ReadingException("Failed to read file " + source.getAbsolutePath(), e);
        }
    }


}
