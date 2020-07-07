package de.bonndan.nivio.observation;

import java.util.concurrent.CompletableFuture;

/**
 * Observer for input sources.
 *
 * URL observer is implemented, but others like k8s observer to be done
 */
interface Observer {

    /**
     * @return a future of the observed whether it had a change
     */
    CompletableFuture<String> hasChange();
}
