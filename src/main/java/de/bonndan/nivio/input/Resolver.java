package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;

/**
 * Resolves data based on the landscape description (input DTO).
 *
 *
 */
public abstract class Resolver {

    protected final ProcessLog processLog;

    protected Resolver(ProcessLog processLog) {
        this.processLog = processLog;
    }

    public abstract void resolve(LandscapeDescription input);
}
