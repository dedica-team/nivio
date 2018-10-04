package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.dto.Environment;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.nivio.ServiceDescriptionFactoryNivio;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EnvironmentFactory {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static Environment fromYaml(File file) {
        FileFetcher fetcher = new FileFetcher(new HttpService());

        try {
            Environment environment = mapper.readValue(file, Environment.class);
            environment.setPath(file.toString());
            environment.getSourceReferences().forEach(ref -> {

                        ServiceDescriptionFactory sdf = ServiceDescriptionFormatFactory.getFactory(ref);
                        ref.setEnvironment(environment);
                        String source = fetcher.get(ref);
                        List<ServiceDescription> serviceDescriptions = sdf.fromString(source);
                        environment.addServices(serviceDescriptions);
                    }
            );

            return environment;
        } catch (IOException e) {
            throw new ReadingException("Failed to create input from " + file.getAbsolutePath(), e);
        }
    }
}
