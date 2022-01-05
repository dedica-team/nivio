package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.Source;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.observation.InputChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Triggers landscape indexing.
 *
 * Factory and launcher for {@link IndexEvent}s
 */
@Service
public class IndexingDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingDispatcher.class);

    private final SeedConfigurationFactory seedConfigurationFactory;
    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ApplicationEventPublisher publisher;
    private final SourceReferencesResolver sourceReferencesResolver;

    public IndexingDispatcher(SeedConfigurationFactory seedConfigurationFactory,
                              LandscapeDescriptionFactory landscapeDescriptionFactory,
                              ApplicationEventPublisher publisher,
                              SourceReferencesResolver sourceReferencesResolver
    ) {
        this.seedConfigurationFactory = seedConfigurationFactory;
        this.landscapeDescriptionFactory = landscapeDescriptionFactory;
        this.publisher = publisher;
        this.sourceReferencesResolver = sourceReferencesResolver;
    }

    @EventListener(SeedConfigurationChangeEvent.class)
    public void onSeedConfigurationChangeEvent(SeedConfigurationChangeEvent event) {
        SeedConfiguration seedConfiguration = event.getConfiguration();
        LOGGER.info("Received index event for config {}", seedConfiguration.getIdentifier());
        handle(seedConfiguration);
    }

    @EventListener(InputChangedEvent.class)
    public void onInputChangedEvent(InputChangedEvent event) {
        URL source = event.getSource();
        LOGGER.info("Received input change for config at {}", source);
        SeedConfiguration seedConfiguration = seedConfigurationFactory.from(source);
        if (seedConfiguration == null) {
            LOGGER.error("Could not handle InputChangedEvent because seed config could not be reconstructed from {}", source);
            return;
        }

        handle(seedConfiguration);
    }

    public LandscapeDescription createLandscapeDescriptionFromBody(@NonNull final String body) {
        LandscapeDescription dto = landscapeDescriptionFactory.fromString(Objects.requireNonNull(body), "request body");
        dto.setIsPartial(false);
        publisher.publishEvent(new IndexEvent(Collections.singletonList(dto), null, "Creating landscape from request body"));
        return dto;
    }

    public LandscapeDescription updateLandscapeDescriptionFromBody(@NonNull final String body, @Nullable String updateIdentifier) {
        LandscapeDescription dto = landscapeDescriptionFactory.fromString(Objects.requireNonNull(body), "request body");
        if (!dto.getIdentifier().equalsIgnoreCase(updateIdentifier)) {
            throw new IllegalArgumentException("Identifier does not match identifier in body.");
        }
        dto.setIsPartial(true);
        publisher.publishEvent(new IndexEvent(Collections.singletonList(dto), null, "Creating landscape from request body"));
        return dto;
    }

    public LandscapeDescription createFromLandscapeDescriptionBodyItems(String identifier, String body) {
        LandscapeDescription dto = landscapeDescriptionFactory.fromBodyItems(identifier, body);
        publisher.publishEvent(new IndexEvent(Collections.singletonList(dto), null, "index landscape"));
        return dto;
    }

    /**
     * Trigger an {@link IndexEvent} using an existing landscape model
     *
     * @param existing the current landscape model
     */
    @NonNull
    public void fromExistingLandscape(@NonNull final Landscape existing) {
        Objects.requireNonNull(existing, "Landscape is null.");
        Source source = existing.getSource();
        if (source == null) {
            throw new ProcessingException(new LandscapeDescription(existing.getIdentifier()), "Cannot create a new landscape description from a landscape without source.");
        }

        SeedConfiguration config = source.getURL()
                .map(seedConfigurationFactory::from)
                .orElseGet(() -> seedConfigurationFactory.fromString(source.getStaticSource(), source));

        handle(config);
    }

    /**
     * Trigger {@link IndexEvent}s for the given config.
     *
     */
    public void handle(@NonNull final SeedConfiguration config) {
        List<LandscapeDescription> landscapes = sourceReferencesResolver.resolve(Objects.requireNonNull(config));
        publisher.publishEvent(new IndexEvent(landscapes, config, String.format("index landscape from %s", config.getIdentifier())));
    }
}
