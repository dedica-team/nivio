package de.bonndan.nivio.input;

import org.springframework.context.ApplicationEvent;

public class SeedConfigurationProcessedEvent extends ApplicationEvent {

    public SeedConfigurationProcessedEvent(SeedConfiguration seedConfiguration) {
        super(seedConfiguration);
    }

    @Override
    public SeedConfiguration getSource() {
        return (SeedConfiguration) super.getSource();
    }
}
