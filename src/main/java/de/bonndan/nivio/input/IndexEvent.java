package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Event is fired to (re)index a landscape.
 */
public class IndexEvent {

    private final List<LandscapeDescription> landscapeDescriptions;
    private final SeedConfiguration seedConfiguration;
    private final String message;

    /**
     * @param landscapeDescriptions landscape descriptions belonging to the config
     * @param seedConfiguration     an optional configuration (might be absent on web-based triggers)
     * @param message               message for the UI
     */
    public IndexEvent(@NonNull final List<LandscapeDescription> landscapeDescriptions,
                      @Nullable final SeedConfiguration seedConfiguration,
                      @Nullable final String message
    ) {
        this.seedConfiguration = seedConfiguration;
        this.landscapeDescriptions = Objects.requireNonNull(landscapeDescriptions);
        this.message = message;
    }

    @NonNull
    public List<LandscapeDescription> getLandscapeDescriptions() {
        return landscapeDescriptions;
    }

    public String getMessage() {
        return message;
    }

    public Optional<SeedConfiguration> getSeedConfiguration() {
        return Optional.ofNullable(seedConfiguration);
    }
}
