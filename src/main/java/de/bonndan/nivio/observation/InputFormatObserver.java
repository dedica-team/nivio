package de.bonndan.nivio.observation;

import java.util.concurrent.CompletableFuture;

/**
 * Observer for input sources.
 *
 * URL observer is implemented, but others like k8s observer to be done
 */
public interface InputFormatObserver {

    /**
     * @return a future of the observed whether it had a change
     * @throws de.bonndan.nivio.ProcessingException on error
     */
    CompletableFuture<String> hasChange();
}
