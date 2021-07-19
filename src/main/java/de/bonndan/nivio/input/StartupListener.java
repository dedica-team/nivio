package de.bonndan.nivio.input;

import de.bonndan.nivio.config.ConfigurableEnvVars;
import de.bonndan.nivio.config.SeedProperties;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * After the application has booted the SEED ({@link Seed}) is processed.
 */
@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ApplicationEventPublisher publisher;
    private final Seed seed;
    private final SeedProperties seedProperties;



    public StartupListener(LandscapeDescriptionFactory landscapeDescriptionFactory,
                           ApplicationEventPublisher publisher,
                           Seed seed,SeedProperties seedProperties
    ) {
        this.landscapeDescriptionFactory = Objects.requireNonNull(landscapeDescriptionFactory);
        this.publisher = Objects.requireNonNull(publisher);
        this.seed = Objects.requireNonNull(seed);
        this.seedProperties = Objects.requireNonNull(seedProperties);
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        if (!StringUtils.isEmpty(seedProperties.getDemo())) {
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