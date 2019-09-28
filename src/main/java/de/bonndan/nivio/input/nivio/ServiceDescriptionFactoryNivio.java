package de.bonndan.nivio.input.nivio;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.input.ServiceDescriptionFactory;
import de.bonndan.nivio.input.dto.ServiceDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.landscape.ServiceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServiceDescriptionFactoryNivio implements ServiceDescriptionFactory {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDescriptionFactoryNivio.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    private final FileFetcher fetcher;
    private final URL baseUrl;

    public ServiceDescriptionFactoryNivio(FileFetcher fetcher, URL baseUrl) {
        this.fetcher = fetcher;
        this.baseUrl = baseUrl;
    }

    public List<ServiceDescription> getDescriptions(SourceReference reference) {

        List<ServiceDescription> services = new ArrayList<>();
        String yml = fetcher.get(reference, baseUrl);
        Source source = null;
        try {
            source = mapper.readValue(yml, Source.class);
        } catch (IOException e) {
            logger.error("Failed to read yml", e);
            throw new ReadingException("Failed to parse yaml service description", e);
        }

        if (source == null) {
            logger.warn("Got null out of yml string " + yml);
            return services;
        }

        if (source.services != null) {
            services.addAll(source.services);
        }

        return services;

    }
}
