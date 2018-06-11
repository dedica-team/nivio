package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class EnvironmentFactory {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static Environment fromYaml(File file) {
        try {
            Environment environment = mapper.readValue(file, Environment.class);
            environment.setPath(file.toString());
            environment.getSources().forEach(source -> source.setEnvironment(environment));
            return environment;
        } catch (IOException e) {
            throw new ReadingException("Failed to create input from " + file.getAbsolutePath(), e);
        }
    }
}
