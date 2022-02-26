package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Resolves data based on the landscape description (input DTO).
 *
 *
 */
public abstract class Resolver {

    protected final ProcessLog processLog;

    protected Resolver(ProcessLog processLog) {
        this.processLog = Objects.requireNonNull(processLog);
    }

    public abstract void resolve(@NonNull final LandscapeDescription input);
}
