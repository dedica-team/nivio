package de.bonndan.nivio.observation;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.FileFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Detects a content change in the given url.
 */
class URLObserver implements Observer {

    private static final Logger LOGGER = LoggerFactory.getLogger(URLObserver.class);

    private final FileFetcher fileFetcher;
    private final ApplicationEventPublisher publisher;
    private final URL url;
    private String content;

    public URLObserver(FileFetcher fileFetcher, ApplicationEventPublisher publisher, URL url) {
        this.fileFetcher = fileFetcher;
        this.publisher = publisher;
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
            if (downloaded == null || downloaded.equals(content)) {
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
            publisher.publishEvent(new ProcessingErrorEvent(this, new ProcessingException("Failed to fetch " + url, e)));
            return null;
        }
    }
}
