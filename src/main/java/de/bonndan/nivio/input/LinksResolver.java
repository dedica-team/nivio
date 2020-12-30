package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Resolves all links of all landscape components.
 */
public class LinksResolver extends Resolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinksResolver.class);

    private final LinkHandlerFactory linkHandlerFactory;

    /**
     * @param logger             the log belonging to the landscape.
     * @param linkHandlerFactory factory responsible to create single link resolvers.
     */
    public LinksResolver(ProcessLog logger, LinkHandlerFactory linkHandlerFactory) {
        super(logger);
        this.linkHandlerFactory = linkHandlerFactory;
    }

    @Override
    public void resolve(LandscapeDescription input) {
        List<CompletableFuture<ComponentDescription>> completableFutures = resolveLinks(input);
        input.getGroups().forEach((s, groupItem) -> {
            resolveLinks(groupItem);
        });
        input.getItemDescriptions().all().forEach(item -> completableFutures.addAll(resolveLinks(item)));

        try {
            LOGGER.info("Waiting for completion of {} external link handlers.", completableFutures.size());
            CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new)).get(2, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            processLog.error(new ProcessingException("Failed to complete all external data resolvers", e));
        }
    }

    private <T extends ComponentDescription> List<CompletableFuture<T>> resolveLinks(T component) {

        List<CompletableFuture<T>> all = new ArrayList<>();
        component.getLinks().forEach((key, link) -> linkHandlerFactory.getResolver(key).ifPresent(handler -> {
                    try {
                        CompletableFuture<T> f = handler
                                .resolve(link)
                                .handleAsync((componentDescription, throwable) -> {
                                    if (componentDescription != null) {
                                        processLog.info(String.format("Successfully read link %s of %s", key, component));
                                        ComponentDescriptionValues.assignSafeNotNull(component, componentDescription);
                                    } else {
                                        LOGGER.warn("Link resolving failure {} {}", key, component, throwable);
                                    }
                                    return component;
                                });
                        all.add(f);
                    } catch (Exception e) {
                        LOGGER.warn("Link resolving failure {} {}", key, component, e);
                        processLog.warn(String.format("Failed read link %s of %s: %s", key, component, e.getMessage()));
                    }
                }

        ));
        return all;
    }


}
