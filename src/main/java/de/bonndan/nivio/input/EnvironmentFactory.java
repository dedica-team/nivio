package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EnvironmentFactory {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public static Environment fromYaml(File file) {

        try {
            Environment environment = mapper.readValue(file, Environment.class);
            environment.setPath(file.toString());
            return process(environment);
        } catch (IOException e) {
            throw new ReadingException("Failed to create an environment from " + file.getAbsolutePath(), e);
        }
    }

    public static Environment fromString(String yaml) {

        Environment environment;

        try {
            environment = mapper.readValue(yaml, Environment.class);
            environment.setPath("yaml string");
            return process(environment);
        } catch (IOException e) {
            throw new ReadingException("Failed to create an environment from yaml string", e);
        }

    }

    private static Environment process(Environment environment) {

        FileFetcher fetcher = new FileFetcher(new HttpService());
        environment.getSourceReferences().forEach(ref -> {

                    ServiceDescriptionFactory sdf = ServiceDescriptionFormatFactory.getFactory(ref.getFormat());
                    ref.setEnvironment(environment);
                    String source = fetcher.get(ref);
                    List<ServiceDescription> serviceDescriptions = sdf.fromString(source);
                    environment.addServices(serviceDescriptions);
                }
        );

        return environment;
    }
}
