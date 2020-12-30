package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Triggers the {@link Indexer} on and {@link IndexEvent}
 */
@Component
public class IndexEventListener implements ApplicationListener<IndexEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexEventListener.class);
    private final Indexer indexer;

    public IndexEventListener(Indexer indexer) {
        this.indexer = indexer;
    }

    @Override
    public void onApplicationEvent(IndexEvent event) {
        LandscapeDescription description = event.getLandscape();
        LOGGER.debug("Received index event for landscape {}", description.getIdentifier());
        indexer.index(description);
    }
}
