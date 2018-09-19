package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.landscape.LandscapeItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDescriptionFactory {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    public static List<ServiceDescription> fromYaml(File file) {

        List<ServiceDescription> services = new ArrayList<>();
        try {
            Source source = mapper.readValue(file, Source.class);
            source.ingress.forEach(serviceDescription -> {
                serviceDescription.setType(LandscapeItem.TYPE_INGRESS);
                services.add(serviceDescription);
            });
            source.services.forEach(serviceDescription -> {
                serviceDescription.setType(LandscapeItem.TYPE_APPLICATION);
                services.add(serviceDescription);
            });
            source.infrastructure.forEach(serviceDescription -> {
                serviceDescription.setType(LandscapeItem.TYPE_INFRASTRUCTURE);
                services.add(serviceDescription);
            });

            return services;
        } catch (IOException e) {
            throw new ReadingException("Failed to create service description from " + file.getAbsolutePath(), e);
        }
    }
}
