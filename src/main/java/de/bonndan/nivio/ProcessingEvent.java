package de.bonndan.nivio;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.bonndan.nivio.model.Landscape;
import org.springframework.context.ApplicationEvent;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Event that is emitted to the frontend in near-realtime during processing of landscapes.
 *
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ProcessingEvent extends ApplicationEvent {

    public static final String LOG_LEVEL_INFO = "info";
    public static final String LOG_LEVEL_ERROR = "error";

    public ProcessingEvent(Object source) {
        super(source);
    }

    @JsonSerialize(using = PLS.class)
    public abstract Landscape getLandscape();

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

    private static class PLS extends JsonSerializer<Landscape> {
        @Override
        public void serialize(Landscape value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getIdentifier());
        }
    }
}
