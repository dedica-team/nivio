package de.bonndan.nivio.observation;

import java.util.concurrent.CompletableFuture;

/**
 * TODO k8s observer etc
 */
interface Observer {

    /**
     * @return a future of the observed whether it had a change
     */
    CompletableFuture<String> hasChange();
}
