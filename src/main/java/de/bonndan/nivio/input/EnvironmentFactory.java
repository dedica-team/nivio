package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnvironmentFactory {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public static Environment fromYaml(File file) {

        try {
            Environment environment = mapper.readValue(file, Environment.class);
            environment.setSource(file.toString());
            environment.getSourceReferences().forEach(ref -> ref.setEnvironment(environment));
            return environment;
        } catch (IOException e) {
            throw new ReadingException("Failed to create an environment from file " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Creates a new environment description and sets the given yaml as source.
     *
     * @param yaml source
     * @return environment description
     */
    public static Environment fromString(String yaml) {

        try {
            Environment environment = mapper.readValue(yaml, Environment.class);
            environment.setSource(yaml);
            environment.getSourceReferences().forEach(ref -> ref.setEnvironment(environment));
            return environment;
        } catch (IOException e) {
            throw new ReadingException("Failed to create an environment from yaml input string", e);
        }

    }

    /**
     * Creates a new environment description and sets the given url as source.
     *
     * @param yaml source
     * @param url for updates
     * @return env description
     */
    public static Environment fromString(String yaml, URL url) {
        Environment env = fromString(yaml);
        env.setSource(url.toString());
        return env;
    }
}
