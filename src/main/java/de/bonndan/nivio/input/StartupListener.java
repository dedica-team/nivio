package de.bonndan.nivio.input;

import de.bonndan.nivio.config.ConfigurableEnvVars;
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

    public StartupListener(LandscapeDescriptionFactory landscapeDescriptionFactory,
                           ApplicationEventPublisher publisher,
                           Seed seed
    ) {
        this.landscapeDescriptionFactory = landscapeDescriptionFactory;
        this.publisher = publisher;
        this.seed = seed;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        if (!StringUtils.isEmpty(ConfigurableEnvVars.DEMO.value().orElse(""))) {
            LOGGER.info("Running in demo mode");
        }

        getUrls(seed).stream()
                .map(landscapeDescriptionFactory::from)
                .forEach(description -> publisher.publishEvent(new IndexEvent(this, description, "Initialising from SEED")));
    }

    private List<URL> getUrls(Seed seed) {
        List<URL> landscapeDescriptionLocations = new ArrayList<>(seed.getDemoFiles());
        for (String s : seed.getLocations()) {
            Optional<URL> tmpURL = URLHelper.getURL(s);
            if (tmpURL.isPresent()) {
                landscapeDescriptionLocations.add(tmpURL.get());
                continue;
            }
            ProcessingException processingException = new ProcessingException(
                    "Failed to initialize watchers from seed",
                    new MalformedURLException("Failed to create URL from " + s)
            );
            publisher.publishEvent(new ProcessingErrorEvent(this, processingException));
        }
        return landscapeDescriptionLocations;
    }
}