package de.bonndan.nivio.landscape;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter
public class LandscapeConfigConverter implements AttributeConverter<LandscapeConfig, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeConfigConverter.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(LandscapeConfig landscapeConfig) {

        try {
            return objectMapper.writeValueAsString(landscapeConfig);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize landscape config", e);
            return null;
        }
    }

    @Override
    public LandscapeConfig convertToEntityAttribute(String json)
    {
        try {
            if (json == null)
                return null;
            return objectMapper.readValue(json, LandscapeConfig.class);
        } catch (IOException e) {
            LOGGER.error("Failed to deserialize landscape config", e);
            return null;
        }
    }
}
