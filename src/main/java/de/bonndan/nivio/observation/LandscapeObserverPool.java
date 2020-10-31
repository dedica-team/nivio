package de.bonndan.nivio.observation;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.model.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A wrapper around observers to reduce the async results to a single boolean.
 */
public class LandscapeObserverPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeObserverPool.class);

    private final Landscape landscape;
    private final List<InputFormatObserver> observers;

    public LandscapeObserverPool(Landscape landscape, List<InputFormatObserver> observers) {
        this.landscape = landscape;
        this.observers = observers;
    }

    /**
     * @return the change
     */
    public ObservedChange getChange() {

        LOGGER.info("Detecting changes in {} observers for landscape {}.", observers.size(), landscape.getIdentifier());

        ObservedChange change = new ObservedChange();
        CompletableFuture<String>[] futures = observers.stream()
                .filter(Objects::nonNull)
                .map(observer -> { //TODO: resolve unchecked assignment
                    try {
                        return observer.hasChange();
                    } catch (ProcessingException e) {
                        change.addError(e);
                        LOGGER.warn("Failed to get change: " + e.getMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures);

        CompletableFuture<List<String>> listCompletableFuture = allDoneFuture.thenApply(
                v -> Stream.of(futures)
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        List<String> changes = listCompletableFuture.join();
        change.setChanges(changes);
        return change;
    }

    Landscape getLandscape() {
        return landscape;
    }

    List<InputFormatObserver> getObservers() {
        return observers;
    }
}
