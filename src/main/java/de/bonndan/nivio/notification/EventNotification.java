package de.bonndan.nivio.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.input.ProcessingEvent;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Api model for internal events. This object is published via http and websockets.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventNotification {

    private final FullyQualifiedIdentifier landscapeIdentifier;
    private final String message;
    private final String level;
    private final String type;
    private final long timestamp;

    /**
     * @param processingEvent application event
     * @return api model
     * @throws NullPointerException
     */
    public static EventNotification from(ProcessingEvent processingEvent) {
        return new EventNotification(
                processingEvent.getSource(),
                processingEvent.getType(),
                processingEvent.getLevel(),
                processingEvent.getTimestamp(),
                processingEvent.getMessage()
        );
    }

    public EventNotification(@NonNull final FullyQualifiedIdentifier landscapeIdentifier,
                             @NonNull final String type,
                             @NonNull final String level,
                             final long timestamp,
                             @Nullable final String message
    ) {
        this.landscapeIdentifier = Objects.requireNonNull(landscapeIdentifier);
        this.message = message;
        this.level = level;
        this.type = type;
        this.timestamp = timestamp;
    }

    /**
     * Returns the log level / severity.
     */
    @NonNull
    public String getLevel() {
        return level;
    }

    /**
     * The event type (usually event class name).
     */
    @NonNull
    public String getType() {
        return type;
    }

    /**
     * An optional message.
     */
    @Nullable
    public String getMessage() {
        return message;
    }

    /**
     * The landscape identifier (can be used as url part).
     */
    public String getLandscape() {
        return landscapeIdentifier.jsonValue();
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    public LocalDateTime getDate() {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
