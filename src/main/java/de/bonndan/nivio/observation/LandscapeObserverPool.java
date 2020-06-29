package de.bonndan.nivio.observation;

import de.bonndan.nivio.model.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A wrapper around observers to reduce the async results to a single boolean.
 *
 *
 */
public class LandscapeObserverPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeObserverPool.class);
    public static final String DELIM = ";";

    private final Landscape landscape;
    private final List<URLObserver> observers;

    public LandscapeObserverPool(Landscape landscape, List<URLObserver> observers) {
        this.landscape = landscape;
        this.observers = observers;
    }

    /**
     * @return the change
     */
    public Optional<String> hasChange() {
        LOGGER.info("Detecting changes in {} observers for landscape {}.", observers.size(), landscape.getIdentifier());

        CompletableFuture<String>[] futures = observers.stream().map(URLObserver::hasChange).toArray(CompletableFuture[]::new);
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures);

        CompletableFuture<List<String>> listCompletableFuture = allDoneFuture.thenApply(
                v -> Stream.of(futures)
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        List<String> changes = listCompletableFuture.join();
        if (!changes.isEmpty()) {
            LOGGER.debug("Detected changes in {} : {}.",  landscape.getIdentifier(), changes);
        }
        return Optional.ofNullable(changes.isEmpty() ? null : StringUtils.collectionToDelimitedString(changes, DELIM));
    }

    public Landscape getLandscape() {
        return landscape;
    }
}
