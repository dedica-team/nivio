package de.bonndan.nivio.input;

import org.springframework.context.ApplicationEvent;

public class ErrorEvent extends ApplicationEvent {
    private final Throwable ex;

    public ErrorEvent(SeedConfiguration configuration, Throwable ex) {
        super(configuration);
        this.ex = ex;
    }

    @Override
    public SeedConfiguration getSource() {
        return (SeedConfiguration) super.getSource();
    }

    public Throwable getEx() {
        return ex;
    }
}
