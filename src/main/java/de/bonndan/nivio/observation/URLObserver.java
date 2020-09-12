package de.bonndan.nivio.observation;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.FileFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Observer for URLs (downloadable files).
 *
 *
 */
public class URLObserver implements InputFormatObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(URLObserver.class);

    private final FileFetcher fileFetcher;
    private final URL url;
    private String content;

    public URLObserver(@NonNull FileFetcher fileFetcher, @NonNull URL url) {
        this.fileFetcher = fileFetcher;
        this.url = url;
        this.content = getContent();
    }

    /**
     * @return a future of the URL whether the URL had a content change
     */
    public CompletableFuture<String> hasChange() {
        LOGGER.debug("Looking for change in url {}", url);
        return CompletableFuture.supplyAsync(() -> {
            String downloaded = getContent();
            if (downloaded.equals(content)) {
                LOGGER.debug("Found no change in url {}", url);
                return null;
            }
            content = downloaded;
            LOGGER.debug("Found change in url {}", url);
            return url.toString();
        });
    }

    private String getContent() {
        try {
            String downloaded = fileFetcher.get(url);
            LOGGER.debug("Downloaded {} bytes from {}", downloaded.length(), url);
            return downloaded;
        } catch (Exception e) {
            throw new ProcessingException("Failed to fetch " + url, e);
        }
    }
}
