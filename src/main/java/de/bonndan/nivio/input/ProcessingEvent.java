package de.bonndan.nivio.input;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Event that is emitted during processing of landscapes.
 */
public abstract class ProcessingEvent extends ApplicationEvent {

    public static final String LOG_LEVEL_INFO = "info";
    public static final String LOG_LEVEL_ERROR = "error";

    public ProcessingEvent(FullyQualifiedIdentifier source) {
        super(source);
    }

    /**
     * Returns the log level / severity.
     */
    public abstract String getLevel();

    /**
     * The event type (usually event class name).
     */
    public abstract String getType();

    /**
     * An optional message.
     */
    public abstract String getMessage();

    public LocalDateTime getDate() {
        Instant instant = Instant.ofEpochMilli(getTimestamp());
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public FullyQualifiedIdentifier getSource() {
        return (FullyQualifiedIdentifier) super.getSource();
    }
}
