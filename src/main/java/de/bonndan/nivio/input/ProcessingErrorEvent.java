package de.bonndan.nivio.input;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;

import java.io.IOException;

/**
 * A processing event that occurred because of an error ({@link ProcessingException} present).
 *
 *
 */
public class ProcessingErrorEvent extends ProcessingEvent {

    private final ProcessingException exception;

    public ProcessingErrorEvent(FullyQualifiedIdentifier fqi, ProcessingException exception) {
        super(fqi);
        this.exception = exception;
    }

    public ProcessingException getException() {
        return exception;
    }

    @JsonSerialize(using = PLS.class)
    public LandscapeDescription getLandscape() {
        return exception.getLandscapeDescription();
    }

    private static class PLS extends JsonSerializer<LandscapeDescription> {
        @Override
        public void serialize(LandscapeDescription value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getIdentifier());
        }
    }

    @Override
    public String getLevel() {
        return LOG_LEVEL_ERROR;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return exception.getMessage();
    }
}
