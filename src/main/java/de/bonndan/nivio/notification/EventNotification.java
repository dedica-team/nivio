package de.bonndan.nivio.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.assessment.AssessmentChangedEvent;
import de.bonndan.nivio.input.ProcessingChangelog;
import de.bonndan.nivio.input.ProcessingEvent;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.observation.InputChangedEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
    private final ProcessingChangelog changelog;

    /**
     * @param processingEvent application event
     * @return api model
     * @throws NullPointerException if a required processing event field is empty
     */
    public static EventNotification from(ProcessingEvent processingEvent) {

        return new EventNotification(
                processingEvent.getSource(),
                processingEvent.getType(),
                processingEvent.getLevel(),
                processingEvent.getTimestamp(),
                processingEvent.getMessage(),
                getChangelog(processingEvent)
        );
    }

    private static ProcessingChangelog getChangelog(ProcessingEvent processingEvent) {
        if (processingEvent instanceof ProcessingFinishedEvent) {
            return ((ProcessingFinishedEvent) processingEvent).getChangelog();
        }

        if (processingEvent instanceof AssessmentChangedEvent) {
            return ((AssessmentChangedEvent) processingEvent).getChangelog();
        }

        return null;
    }

    public static EventNotification from(InputChangedEvent inputChangedEvent) {
        return new EventNotification(
                null,
                InputChangedEvent.class.getSimpleName(),
                ProcessingEvent.LOG_LEVEL_INFO,
                inputChangedEvent.getTimestamp(),
                String.join("; ", inputChangedEvent.getObservedChange().getChanges()),
                null
        );
    }

    private EventNotification(
            @Nullable final FullyQualifiedIdentifier landscapeIdentifier,
            @NonNull final String type,
            @NonNull final String level,
            final long timestamp,
            @Nullable final String message,
            @Nullable final ProcessingChangelog changelog
    ) {
        this.landscapeIdentifier = landscapeIdentifier;
        this.message = message;
        this.level = level;
        this.type = type;
        this.timestamp = timestamp;
        this.changelog = changelog;
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

    @Schema(description = "The landscape identifier (can be used as url part)")
    public String getLandscape() {
        return landscapeIdentifier != null ? landscapeIdentifier.jsonValue() : "";
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    public ZonedDateTime getDate() {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Schema(description = "In case of ProcessingFinishedEvent a changelog is contained.")
    @Nullable
    public ProcessingChangelog getChangelog() {
        return changelog;
    }
}
