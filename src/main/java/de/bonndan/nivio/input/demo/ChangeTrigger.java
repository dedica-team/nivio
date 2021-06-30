package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.config.ConfigurableEnvVars;
import de.bonndan.nivio.input.IndexEvent;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

@Component
public class ChangeTrigger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeTrigger.class);

    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ApplicationEventPublisher eventPublisher;

    public ChangeTrigger(LandscapeDescriptionFactory landscapeDescriptionFactory,
                         ApplicationEventPublisher eventPublisher
    ) {
        this.landscapeDescriptionFactory = landscapeDescriptionFactory;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(initialDelay = 20000, fixedDelay = 30000)
    public void trigger() {

        if (ConfigurableEnvVars.DEMO.value().isEmpty()) {
            LOGGER.debug("DEMO not set, not simulating any pet clinic events.");
            return;
        }

        getDemoLandscapeDescription().ifPresent(petClinic -> eventPublisher.publishEvent(new IndexEvent(petClinic, "Demo update")));
    }

    private Optional<LandscapeDescription> getDemoLandscapeDescription() {
        String absPath = Paths.get("").toAbsolutePath().toString();
        String demoFile = absPath + "/src/test/resources/example/pet_clinic.yml";
        File file = new File(demoFile);
        if (!file.exists()) {
            LOGGER.error(String.format("Failed to read pet clinic demo data from %s", demoFile));
            return Optional.empty();
        }

        return Optional.of(landscapeDescriptionFactory.fromYaml(file));
    }
}
