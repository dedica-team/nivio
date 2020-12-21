package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;

/**
 * Modifies the target landscape using input.
 *
 *
 */
abstract class Processor {

    protected final ProcessLog processLog;

    protected Processor(ProcessLog processLog) {
        this.processLog = processLog;
    }

    public abstract void process(LandscapeDescription input, Landscape landscape);
}
