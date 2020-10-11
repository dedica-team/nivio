package de.bonndan.nivio;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;

import java.io.IOException;

/**
 * Event is fired after successful indexing of a landscape.
 *
 *
 *
 */
public class ProcessingFinishedEvent extends ProcessingEvent {

    private final Landscape landscape;

    /**
     * @param source the LandscapeDescription
     * @param landscape out
     */
    public ProcessingFinishedEvent(LandscapeDescription source, Landscape landscape) {
        super(source);
        this.landscape = landscape;
    }

    @JsonSerialize(using = PLS.class)
    public Landscape getLandscape() {
        return landscape;
    }

    private static class PLS extends JsonSerializer<Landscape> {
        @Override
        public void serialize(Landscape value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getIdentifier());
        }
    }

    @Override
    public String getLevel() {
        return LOG_LEVEL_INFO;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return null;
    }
}
