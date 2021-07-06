package de.bonndan.nivio.input;

import de.bonndan.nivio.config.ConfigurableEnvVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * After the application has booted the SEED ({@link Seed}) is processed.
 */
@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

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
    public void onApplicationEvent(@NonNull final ApplicationReadyEvent event) {
        if (!StringUtils.isEmpty(ConfigurableEnvVars.DEMO.value().orElse(""))) {
            LOGGER.info("Running in demo mode");
        }

        getUrls(seed).stream()
                .map(landscapeDescriptionFactory::from)
                .forEach(description -> publisher.publishEvent(new IndexEvent(description, "Initialising from SEED")));
    }

    private List<URL> getUrls(Seed seed) {
        List<URL> landscapeDescriptionLocations = new ArrayList<>(seed.getDemoFiles());
        landscapeDescriptionLocations.addAll(seed.getLocations());
        return landscapeDescriptionLocations;
    }
}