package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.util.URLHelper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Triggers landscape indexing.
 *
 * Factory and launcher for {@link IndexEvent}s
 */
@Service
public class IndexingDispatcher {

    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ApplicationEventPublisher publisher;

    public IndexingDispatcher(LandscapeDescriptionFactory landscapeDescriptionFactory,
                              ApplicationEventPublisher publisher
    ) {
        this.landscapeDescriptionFactory = landscapeDescriptionFactory;
        this.publisher = publisher;
    }

    public LandscapeDescription createFromBody(String body) {
        LandscapeDescription env = landscapeDescriptionFactory.fromString(body, "request body");
        publisher.publishEvent(new IndexEvent(env, "Creating landscape from request body"));
        return env;
    }

    public LandscapeDescription createFromBodyItems(String identifier, String format, String body) {
        LandscapeDescription dto = landscapeDescriptionFactory.fromBodyItems(identifier, format, body);
        publisher.publishEvent(new IndexEvent(dto, "index landscape"));
        return dto;
    }

    /**
     * Trigger an {@link IndexEvent} using an existing landscape model
     *
     * @param existing the current landscape model
     * @return a generated landscape description
     */
    public LandscapeDescription fromIncoming(@NonNull final Landscape existing) {
        Objects.requireNonNull(existing, "Landscape is null.");
        LandscapeDescription dto = URLHelper.getURL(existing.getSource())
                .map(landscapeDescriptionFactory::from)
                .orElse(landscapeDescriptionFactory.fromString(existing.getSource(), existing.getIdentifier() + " source"));

        IndexEvent event = new IndexEvent(dto, "index landscape");
        publisher.publishEvent(event);
        return dto;
    }
}
