package de.bonndan.nivio.input;

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
import java.util.Objects;

/**
 * After the application has booted the SEED ({@link Seed}) is processed.
 *
 * Publishes {@link SeedConfigurationChangeEvent}s for each of the resolved seed urls/files.
 */
@Component
public class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

    private final SeedConfigurationFactory configurationFactory;
    private final ApplicationEventPublisher publisher;
    private final Seed seed;

    public StartupListener(SeedConfigurationFactory configurationFactory,
                           ApplicationEventPublisher publisher,
                           Seed seed
    ) {
        this.configurationFactory = Objects.requireNonNull(configurationFactory);
        this.publisher = Objects.requireNonNull(publisher);
        this.seed = Objects.requireNonNull(seed);
    }

    @Override
    public void onApplicationEvent(@NonNull final ApplicationReadyEvent event) {
        if (StringUtils.hasLength(seed.getDemo())) {
            LOGGER.info("Running in demo mode");
        }

        getUrls(seed).stream()
                .map(configurationFactory::from)
                .filter(Objects::nonNull)
                .forEach(seedConfiguration -> publisher.publishEvent(new SeedConfigurationChangeEvent(seedConfiguration, "Initialising from SEED")));
    }

    private List<URL> getUrls(Seed seed) {
        List<URL> configurations = new ArrayList<>(seed.getDemoFiles());
        configurations.addAll(seed.getLocations());
        return configurations;
    }
}