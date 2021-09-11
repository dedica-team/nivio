package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

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

    public abstract void resolve(@NonNull final LandscapeDescription input);
}
