package de.bonndan.nivio.input;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Configuration of a YAML object mapper.
 */
public class Mappers {

    private Mappers(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(Mappers.class);

    public static final ObjectMapper gracefulYamlMapper = new ObjectMapper(new YAMLFactory());

    private static final UnMarshallingErrorHandler errorHandler = new UnMarshallingErrorHandler();

    static {
        gracefulYamlMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        gracefulYamlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        gracefulYamlMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        gracefulYamlMapper.addHandler(errorHandler);
    }

    static class UnMarshallingErrorHandler extends DeserializationProblemHandler {

        private static final Map<String, String> mapping = Map.of(
                LandscapeDescription.class.getSimpleName(), "landscape",
                UnitDescription.class.getSimpleName(), "unit",
                ContextDescription.class.getSimpleName(), "context",
                GroupDescription.class.getSimpleName(), "group",
                ItemDescription.class.getSimpleName(), "item",
                SourceReference.class.getSimpleName(), "source"
        );

        @Override
        public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp, JsonDeserializer deserializer, Object beanOrClass, String propertyName) throws IOException {
            super.handleUnknownProperty(ctxt, jp, deserializer, beanOrClass, propertyName);
            String simpleName = beanOrClass.getClass().getSimpleName();
            LOGGER.error("Property with name '{}' doesn't exist in '{}'", propertyName, mapping.getOrDefault(simpleName, simpleName));
            throw JsonMappingException.from(ctxt, String.format("Property with name %s doesn't exist", propertyName));
        }

    }
}
