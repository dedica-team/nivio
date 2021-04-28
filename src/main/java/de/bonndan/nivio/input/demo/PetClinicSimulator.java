package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.config.ConfigurableEnvVars;
import de.bonndan.nivio.input.IndexEvent;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Simulates a pet clinic landscape with changing values.
 */
@Component
public class PetClinicSimulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PetClinicSimulator.class);

    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ApplicationEventPublisher eventPublisher;

    public PetClinicSimulator(LandscapeDescriptionFactory landscapeDescriptionFactory,
                              ApplicationEventPublisher eventPublisher
    ) {
        this.landscapeDescriptionFactory = landscapeDescriptionFactory;
        this.eventPublisher = eventPublisher;
    }

    public void simulateSomething() {

        if (ConfigurableEnvVars.DEMO.value().isEmpty()) {
            return;
        }

        getDemoLandscapeDescription().ifPresent(petClinic -> {
            modifySomething(petClinic);
            eventPublisher.publishEvent(new IndexEvent(petClinic, "Demo update"));
        });
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

    /**
     * For now it only changes the scaling of a service.
     */
    private void modifySomething(final LandscapeDescription landscapeDescription) {
        List<ItemDescription> all = new ArrayList<>(landscapeDescription.getItemDescriptions().all());
        Collections.shuffle(all);
        Optional.ofNullable(all.get(0)).ifPresent(itemDescription -> {
            int scale = Integer.parseInt(itemDescription.getLabel(Label.scale));
            if (scale <= 0) {
                itemDescription.setLabel(Label.scale, "1");
            } else {
                itemDescription.setLabel(Label.scale, "0");
            }
            LOGGER.info("Simulating scale {}  on item {}", itemDescription.getLabel(Label.scale), itemDescription);
        });
    }
}
