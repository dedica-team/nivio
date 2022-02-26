package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.*;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

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
    public void resolve(@NonNull final LandscapeDescription input) {
        List<CompletableFuture<ComponentDescription>> completableFutures = resolveLinks(input);
        input.getReadAccess().all(UnitDescription.class).stream()
                .map(this::resolveLinks)
                .forEach(completableFutures::addAll);
        input.getReadAccess().all(ContextDescription.class).stream()
                .map(this::resolveLinks)
                .forEach(completableFutures::addAll);
        input.getReadAccess().all(GroupDescription.class).stream()
                .map(this::resolveLinks)
                .forEach(completableFutures::addAll);
        input.getReadAccess().all(ItemDescription.class).stream()
                .map(this::resolveLinks).forEach(completableFutures::addAll);


        LOGGER.info("Waiting for completion of {} external link handlers.", completableFutures.size());
        try {
            CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new)).get(2, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            processLog.error(new ProcessingException(input, "Failed to complete all external data resolvers", e));
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private  List<CompletableFuture<ComponentDescription>> resolveLinks(final ComponentDescription dto) {

        List<CompletableFuture<ComponentDescription>> all = new ArrayList<>();
        dto.getLinks().forEach((key, link) -> linkHandlerFactory.getResolver(key).ifPresent(handler -> {
                    LOGGER.debug("Resolving link {} of component {}", link, dto);
                    try {
                        CompletableFuture<ComponentDescription> f = handler
                                .resolve(link)
                                .handleAsync((componentDescription, throwable) -> {
                                    if (componentDescription != null) {
                                        processLog.info(String.format("Successfully read link %s of %s", key, dto));
                                        dto.assignSafeNotNull(componentDescription);
                                    } else {
                                        LOGGER.warn("Link resolving failure {} {}", key, dto, throwable);
                                    }
                                    return dto;
                                });
                        all.add(f);
                    } catch (Exception e) {
                        LOGGER.warn("Link resolving failure {} {}", key, dto, e);
                        processLog.warn(String.format("Failed read link %s of %s: %s", key, dto, e.getMessage()));
                    }
                }

        ));
        return all;
    }


}
