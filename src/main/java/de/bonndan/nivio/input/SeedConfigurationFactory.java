package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.dto.Source;
import de.bonndan.nivio.util.URLFactory;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A factory to create {@link SeedConfiguration}s from files or strings.
 *
 *
 */
@Component
public class SeedConfigurationFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeedConfigurationFactory.class);
    private static final ObjectMapper mapper = Mappers.gracefulYamlMapper;

    private final FileFetcher fileFetcher;

    public SeedConfigurationFactory(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    /**
     * Creates a dto from a URL by fetching its target.
     *
     * @param url to url of the source
     * @return a landscape description
     */
    @Nullable
    public SeedConfiguration from(@NonNull final URL url) {
        return fromString(fileFetcher.get(url), url);
    }

    /**
     * @param file yaml file object
     * @return the descriptions or throws
     * @throws ReadingException on error
     */
    @NonNull
    public SeedConfiguration fromFile(File file) {

        String content = FileFetcher.readFile(file);
        SeedConfiguration configuration;
        try {
            configuration = fromString(content, new Source(file.toURI().toURL()));
        } catch (MalformedURLException e1) {
            LOGGER.error("Failed to convert file {} to URL", file, e1);
            configuration = fromString(content, new Source(content));
        }

        try {
            URL url = file.toURI().toURL();
            Source source = new Source(url);
            configuration.setSource(source);
            configuration.setBaseUrl(getBaseUrl(source));
        } catch (MalformedURLException e) {
            LOGGER.warn("Could not set source from file {}", file);
        }
        for (SourceReference ref : configuration.getSourceReferences()) {
            ref.setConfig(configuration);
        }
        return configuration;
    }

    /**
     * Creates a new environment description and sets the given yaml as source.
     *
     * @param content source
     * @param source  origin of the yaml for debugging
     * @return environment description
     * @throws ReadingException on error
     */
    @NonNull
    public SeedConfiguration fromString(String content, Source source) {

        if (!StringUtils.hasLength(content)) {
            throw new ReadingException("Failed to create an environment from empty yaml input string.", new IllegalArgumentException("Got an empty string."));
        }

        content = (new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup())).replace(content);

        try {
            SeedConfiguration config = mapper.readValue(content, SeedConfiguration.class);
            config.setSource(source);
            config.setBaseUrl(getBaseUrl(source));
            config.getSourceReferences().forEach(ref -> ref.setConfig(config));
            sanitizeTemplates(config);
            return config;
        } catch (JsonMappingException e) {
            throw ReadingException.fromMappingException(source.get(), e);
        } catch (IOException e) {
            throw new ReadingException(String.format("Failed to create an environment from yaml input string: %s", e.getMessage()), e);
        }
    }

    /**
     * Creates a new environment description and sets the given url as source.
     *
     * @param yaml source
     * @param url  for updates
     * @return env description
     * @throws ReadingException on error
     */
    @NonNull
    public SeedConfiguration fromString(String yaml, @NonNull URL url) {
        SeedConfiguration env = fromString(yaml, new Source(url));
        env.setSource(new Source(url));
        return env;
    }

    /**
     * sanitize templates, unset properties which are not reusable
     */
    private static void sanitizeTemplates(SeedConfiguration landscapeDescription) {
        if (landscapeDescription.getTemplates() != null) {
            landscapeDescription.getTemplates().forEach((s, tpl) -> tpl.setName(""));
        }
    }

    private URL getBaseUrl(Source source) {
        if (source != null) {
            return source.getURL().flatMap(URLFactory::getParentPath).orElse(null);
        }
        return null;
    }
}
