package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.input.IndexingDispatcher;
import de.bonndan.nivio.input.SeedConfiguration;
import de.bonndan.nivio.input.SeedConfigurationFactory;
import de.bonndan.nivio.model.LandscapeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class ChangeTrigger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeTrigger.class);

    private final SeedConfigurationFactory configurationFactory;
    private final LandscapeRepository landscapeRepository;
    private final IndexingDispatcher indexingDispatcher;

    public ChangeTrigger(
            LandscapeRepository landscapeRepository,
            SeedConfigurationFactory configurationFactory,
            IndexingDispatcher indexingDispatcher
    ) {
        this.configurationFactory = configurationFactory;
        this.landscapeRepository = landscapeRepository;
        this.indexingDispatcher = indexingDispatcher;
    }

    @Scheduled(initialDelay = 20000, fixedDelay = 30000)
    public void trigger() {
        if (StreamSupport.stream(landscapeRepository.findAll().spliterator(), false).noneMatch(landscape -> landscape.getIdentifier().equals("petclinic"))) {
            LOGGER.debug("DEMO not set, not simulating any pet clinic events.");
            return;
        }

        getDemoLandscapeDescription().ifPresent(indexingDispatcher::handle);
    }

    private Optional<SeedConfiguration> getDemoLandscapeDescription() {
        String absPath = Paths.get("").toAbsolutePath().toString();
        String demoFile = absPath + "/src/test/resources/example/pet_clinic.yml";
        File file = new File(demoFile);
        if (!file.exists()) {
            LOGGER.error("Failed to read pet clinic demo data from {}", demoFile);
            return Optional.empty();
        }


        return Optional.of(configurationFactory.fromFile(file));
    }
}
