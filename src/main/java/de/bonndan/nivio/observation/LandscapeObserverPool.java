package de.bonndan.nivio.observation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A wrapper around observers to reduce the async results to a single boolean.
 *
 *
 */
public class LandscapeObserverPool {

    private final List<URLObserver> observers;

    public LandscapeObserverPool(List<URLObserver> observers) {
        this.observers = observers;
    }

    /**
     * @return the url that has changed
     */
    public Optional<String> hasChange() {
        return observers.parallelStream()
                .map(URLObserver::hasChange) //trigger all change detections
                .map(CompletableFuture::join) //collect all results
                .filter(Objects::nonNull)
                .findFirst(); //returns the first if at least one has changed
    }
}
