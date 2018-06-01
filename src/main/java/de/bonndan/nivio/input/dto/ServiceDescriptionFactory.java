package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.ReadingException;

import java.io.File;
import java.io.IOException;

public class ServiceDescriptionFactory {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    public static ServiceDescription fromYaml(File file) {
        try {
            return mapper.readValue(file, ServiceDescription.class);
        } catch (IOException e) {
            throw new ReadingException("Failed to create service description from " + file.getAbsolutePath(), e);
        }
    }
}
