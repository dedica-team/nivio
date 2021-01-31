package de.bonndan.nivio.input;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.bonndan.nivio.input.dto.LandscapeDescription;

import java.io.IOException;

/**
 * Event is fired to (re)index a landscape.
 */
public class IndexEvent extends ProcessingEvent {

    private final String message;
    private final LandscapeDescription landscapeDescription;

    /**
     * @param source triggering class
     * @param landscapeDescription landscape description
     * @param message message for the UI
     */
    public IndexEvent(Object source, LandscapeDescription landscapeDescription, String message) {
        super(source);
        if (landscapeDescription == null) {
            throw new IllegalArgumentException("The IndexEvent must contain a landscape description.");
        }
        this.landscapeDescription = landscapeDescription;
        this.message = message;
    }

    /**
     * @return the {@link LandscapeDescription}, not the landscape!
     */
    @JsonSerialize(using = PLS.class)
    public LandscapeDescription getLandscapeDescription() {
        return landscapeDescription;
    }

    private static class PLS extends JsonSerializer<LandscapeDescription> {
        @Override
        public void serialize(LandscapeDescription value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
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
        return message;
    }
}
