package de.bonndan.nivio.input;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Event that is fired when a configuration needs to be processed.
 */
public class SeedConfigurationChangeEvent extends ApplicationEvent {

    private final String message;

    public SeedConfigurationChangeEvent(@NonNull final SeedConfiguration source, @Nullable final String message) {
        super(source);
        this.message = message;
    }

    public SeedConfiguration getConfiguration() {
        return (SeedConfiguration) getSource();
    }

    public String getMessage() {
        return message;
    }
}
