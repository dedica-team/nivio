package de.bonndan.nivio.input.compose2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ServiceDescriptionFactory;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServiceDescriptionFactoryCompose2 implements ServiceDescriptionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDescriptionFactory.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    private final FileFetcher fileFetcher;
    private final URL baseUrl;

    public ServiceDescriptionFactoryCompose2(FileFetcher fileFetcher, URL baseUrl) {
        this.fileFetcher = fileFetcher;
        this.baseUrl = baseUrl;
    }

    public List<ItemDescription> getDescriptions(SourceReference reference) {

        List<ItemDescription> services = new ArrayList<>();
        String yml = fileFetcher.get(reference, baseUrl);
        DockerComposeFile source = null;
        try {
            source = mapper.readValue(yml, DockerComposeFile.class);
        } catch (IOException e) {
            logger.error("Failed to read yml", e);
        }
        if (source == null) {
            logger.warn("Got null out of yml string " + yml);
            return services;
        }

        source.services.forEach((identifier, composeService) -> {
            composeService.setIdentifier(identifier);
            services.add(composeService.toDto());
        });

        return services;

    }

}
