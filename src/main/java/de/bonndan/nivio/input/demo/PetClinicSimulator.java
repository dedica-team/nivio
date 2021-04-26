package de.bonndan.nivio.input.demo;

import de.bonndan.nivio.config.ConfigurableEnvVars;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Simulates a pet clinic landscape in a kubernetes context.
 */
@Component
public class PetClinicSimulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PetClinicSimulator.class);

    private final LandscapeRepository landscapeRepository;

    public PetClinicSimulator(LandscapeRepository landscapeRepository) {
        this.landscapeRepository = landscapeRepository;
    }

    public void simulateSomething() {
        if (ConfigurableEnvVars.DEMO.value().isEmpty()) {
            return;
        }

        Optional<Landscape> petclinic = landscapeRepository.findDistinctByIdentifier("petclinic");
    }

}
