package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.input.ProcessingEvent;
import de.bonndan.nivio.model.Landscape;
import org.springframework.lang.NonNull;

public class LayoutChangedEvent extends ProcessingEvent {

    @NonNull
    private final String msg;

    public LayoutChangedEvent(@NonNull final Landscape landscape, @NonNull final String msg) {
        super(landscape.getFullyQualifiedIdentifier());
        this.msg = msg;
    }

    @Override
    public String getLevel() {
        return ProcessingEvent.LOG_LEVEL_INFO;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
