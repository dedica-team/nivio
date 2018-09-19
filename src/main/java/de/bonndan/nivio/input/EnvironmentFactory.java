package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.ServiceDescriptionFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EnvironmentFactory {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static Environment fromYaml(File file) {
        try {
            Environment environment = mapper.readValue(file, Environment.class);
            environment.setPath(file.toString());
            environment.getSources().forEach(s -> {
                        s.setEnvironment(environment);
                        List<ServiceDescription> serviceDescriptions = ServiceDescriptionFactory.fromYaml(new File(s.getFullUrl()));
                        environment.addServices(serviceDescriptions);
                    }
            );

            for (Source source : environment.getSources()) {

            }
            return environment;
        } catch (IOException e) {
            throw new ReadingException("Failed to create input from " + file.getAbsolutePath(), e);
        }
    }
}
