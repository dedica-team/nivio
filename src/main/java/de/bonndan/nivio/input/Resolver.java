package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.springframework.lang.NonNull;

/**
 * Resolves data based on the landscape description (input DTO) and possibly mutates the data.
 *
 *
 */
@FunctionalInterface
public interface Resolver {

    @NonNull
    LandscapeDescription resolve(@NonNull final LandscapeDescription input);
}
