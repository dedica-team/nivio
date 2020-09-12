package de.bonndan.nivio.observation;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.dto.SourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Observer for source references which point to files.
 */
public class FileSourceReferenceObserver implements InputFormatObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSourceReferenceObserver.class);

    private final FileFetcher fileFetcher;
    private final SourceReference reference;
    private final URL url;
    private String content;

    public FileSourceReferenceObserver(FileFetcher fileFetcher, SourceReference reference, URL url) {
        this.fileFetcher = fileFetcher;
        this.reference = reference;
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
            String downloaded = fileFetcher.get(reference, url);
            LOGGER.debug("Downloaded {} bytes from {}", downloaded.length(), url);
            return downloaded;
        } catch (Exception e) {
            throw new ProcessingException("Failed to fetch " + url, e);
        }
    }

}
