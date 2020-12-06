package de.bonndan.nivio.input.linked;

import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Link;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for resolvers that read external data from {@link Link}s and set labelss on a component.
 * <p>
 * This interface design requires state in the link resolver (after having read the link). Maybe it can be done better.
 */
public interface ExternalLinkHandler {

    /**
     * Follows the links and stores data then applies the data to the component.
     */
    CompletableFuture<String> resolveAndApplyData(Link link, Labeled component);

}
