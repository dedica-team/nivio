package de.bonndan.nivio.input;

import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Link;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for resolvers that read external data from {@link Link}s and set labelss on a component.
 *
 * This interface design requires state in the link resolver (after having read the link). Maybe it can be done better.
 */
public interface LinkResolver {

    /**
     * Follows the links and stores data.
     *
     */
    CompletableFuture<LinkResolver> resolve(Link link);

    /**
     * Applies the data from the internal state to the component.
     */
    void applyData(Labeled component);
}
