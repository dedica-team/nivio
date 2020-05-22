package de.bonndan.nivio.input;

import de.bonndan.nivio.IndexEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * After the application has booted the SEED ({@link Seed}) is processed.
 *
 *
 */
@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ApplicationEventPublisher publisher;
    private final Seed seed;

    public StartupListener(LandscapeDescriptionFactory landscapeDescriptionFactory,
                           ApplicationEventPublisher publisher,
                           Seed seed
    ) {
        this.landscapeDescriptionFactory = landscapeDescriptionFactory;
        this.publisher = publisher;
        this.seed = seed;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (seed.hasValue()) {
            LOGGER.debug("Found seed");
        } else if (!StringUtils.isEmpty(System.getenv(Seed.DEMO))) {
            LOGGER.info("Running in demo mode");
        }

        landscapeDescriptionFactory.getDescriptions(seed).forEach(landscapeDescription -> {
            publisher.publishEvent(new IndexEvent(this, landscapeDescription, "Initialising from SEED"));
        });
    }
}