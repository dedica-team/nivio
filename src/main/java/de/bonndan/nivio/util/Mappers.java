package de.bonndan.nivio.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Mappers {

    public static final ObjectMapper gracefulYamlMapper = new ObjectMapper(new YAMLFactory());

    static {
        gracefulYamlMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        gracefulYamlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        gracefulYamlMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }
}
