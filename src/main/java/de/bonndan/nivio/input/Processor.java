package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

/**
 * Modifies the target landscape using input.
 */
abstract class Processor {

    protected final ProcessLog processLog;

    protected Processor(ProcessLog processLog) {
        this.processLog = processLog;
    }

    /**
     * Apply the input to the landscape.
     *
     * @param input     input data
     * @param landscape the landscape to be modified
     * @return a map of changes containing {@link de.bonndan.nivio.model.FullyQualifiedIdentifier} as keys
     */
    public abstract ProcessingChangelog process(@NonNull final LandscapeDescription input, @NonNull final Landscape landscape);
}
