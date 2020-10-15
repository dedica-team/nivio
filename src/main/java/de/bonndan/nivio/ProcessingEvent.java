package de.bonndan.nivio;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Event that is emitted to the frontend in near-realtime during processing of landscapes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ProcessingEvent extends ApplicationEvent {

    public static final String LOG_LEVEL_INFO = "info";
    public static final String LOG_LEVEL_ERROR = "error";

    public ProcessingEvent(Object source) {
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    public LocalDateTime getDate() {
        Instant instant = Instant.ofEpochMilli(getTimestamp());
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    @JsonIgnore
    public Object getSource() {
        return super.getSource();
    }
}
