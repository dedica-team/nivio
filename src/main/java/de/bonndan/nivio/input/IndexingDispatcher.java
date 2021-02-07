package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
        IndexEvent event = new IndexEvent(this, env, "create landscape");
        publisher.publishEvent(event);
        return env;
    }

    public LandscapeDescription createFromBodyItems(String identifier, String format, String body) {
        LandscapeDescription dto = landscapeDescriptionFactory.fromBodyItems(identifier, format, body);

        IndexEvent event = new IndexEvent(this, dto, "index landscape");
        publisher.publishEvent(event);
        return dto;
    }

    public LandscapeDescription fromIncoming(Landscape existing) {
        LandscapeDescription dto = landscapeDescriptionFactory.fromString(
                existing.getSource(),
                existing.getIdentifier() + " source"
        );
        IndexEvent event = new IndexEvent(this, dto, "index landscape");
        publisher.publishEvent(event);
        return dto;
    }
}
