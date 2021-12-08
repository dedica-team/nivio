package de.bonndan.nivio.input.external;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.model.Link;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for resolvers that read external data from {@link Link}s.
 */
public interface ExternalLinkHandler {

    /**
     * Follows the link and creates a new input DTO to be merged into the target item.
     */
    CompletableFuture<ComponentDescription> resolve(Link link);
}
