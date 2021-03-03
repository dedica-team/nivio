package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Observer for URLs (downloadable files).
 */
public class RemoteURLObserver extends BaseObserver  {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteURLObserver.class);

    private final FileFetcher fileFetcher;
    private final URL url;
    private String content;

    public RemoteURLObserver(@NonNull final Landscape landscape,
                             @NonNull final ApplicationEventPublisher eventPublisher,
                             @NonNull final FileFetcher fileFetcher,
                             @NonNull final URL url
    ) {
        super(landscape, eventPublisher);
        if (URLHelper.isLocal(url)) {
            throw new IllegalArgumentException(String.format("Given url is local: %s, use FileObserver instead.", url));
        }
        this.fileFetcher = Objects.requireNonNull(fileFetcher);
        this.url = Objects.requireNonNull(url);
        this.content = getContent();
    }

    @Override
    public void run() {
        LOGGER.debug("Looking for change in url {}", url);

        String downloaded = getContent();
        if (downloaded.equals(content)) {
            LOGGER.debug("Found no change in url {}", url);
            return;
        }
        content = downloaded;
        LOGGER.debug("Found change in url {}", url);
        eventPublisher.publishEvent(new InputChangedEvent(new ObservedChange(landscape, url.toString())));
    }

    private String getContent() {
        try {
            String downloaded = fileFetcher.get(url);
            if (downloaded != null) {
                LOGGER.debug("Downloaded {} bytes from {}", downloaded.length(), url);
            }
            return downloaded;
        } catch (Exception e) {
            throw new ProcessingException("Failed to fetch " + url, e);
        }
    }
}
