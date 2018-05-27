package de.bonndan.nivio.applayer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class ServiceFactory {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static Service fromYaml(File file) {
        try {
            return mapper.readValue(file, Service.class);
        } catch (IOException e) {
            throw new ServiceReadingException("Failed to create service from " + file.getAbsolutePath(), e);
        }
    }
}
