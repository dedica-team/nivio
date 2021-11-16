package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.LandscapeSource;
import de.bonndan.nivio.input.dto.SourceReference;
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
import java.util.List;

/**
 * A factory to create Landscape DTO instances from files or strings.
 */
@Component
public class LandscapeDescriptionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeDescriptionFactory.class);
    private static final ObjectMapper mapper = Mappers.gracefulYamlMapper;

    private final FileFetcher fileFetcher;

    public LandscapeDescriptionFactory(FileFetcher fileFetcher) {
        this.fileFetcher = fileFetcher;
    }

    /**
     * Creates a dto from a URL by fetching its target.
     *
     * @param url to url of the source
     * @return a landscape description
     */
    @Nullable
    public LandscapeDescription from(@NonNull final URL url) {
        return fromString(fileFetcher.get(url), url);
    }

    /**
     * @param file yaml file object
     * @return the descriptions or throws
     * @throws ReadingException on error
     */
    @NonNull
    public LandscapeDescription fromYaml(File file) {

        String content = fileFetcher.get(file);
        LandscapeDescription landscapeDescription = fromString(content, file.toString());
        try {
            landscapeDescription.setSource(new LandscapeSource(file.toURI().toURL()));
        } catch (MalformedURLException e) {
            LOGGER.warn("Could not set source from file {}", file);
        }
        landscapeDescription.getSourceReferences().forEach(ref -> ref.setLandscapeDescription(landscapeDescription));
        return landscapeDescription;
    }

    /**
     * Creates a new environment description and sets the given yaml as source.
     *
     * @param yaml   source
     * @param origin origin of the yaml for debugging
     * @return environment description
     * @throws ReadingException on error
     */
    @NonNull
    public LandscapeDescription fromString(String yaml, String origin) {

        if (!StringUtils.hasLength(yaml)) {
            throw new ReadingException("Failed to create an environment from empty yaml input string.", new IllegalArgumentException("Got an empty string."));
        }

        yaml = (new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup())).replace(yaml);

        try {
            LandscapeDescription landscapeDescription = mapper.readValue(yaml, LandscapeDescription.class);
            landscapeDescription.setSource(new LandscapeSource(yaml));
            landscapeDescription.getSourceReferences().forEach(ref -> ref.setLandscapeDescription(landscapeDescription));
            sanitizeTemplates(landscapeDescription);
            return landscapeDescription;
        } catch (JsonMappingException e) {
            throw ReadingException.from(origin, e);
        } catch (IOException e) {
            throw new ReadingException("Failed to create an environment from yaml input string: " + e.getMessage(), e);
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
    public LandscapeDescription fromString(String yaml, @NonNull URL url) {
        LandscapeDescription env = fromString(yaml, url.toString());
        env.setSource(new LandscapeSource(url));
        return env;
    }

    @NonNull
    public LandscapeDescription fromBodyItems(String identifier, String format, String body) {
        LandscapeDescription dto = new LandscapeDescription(identifier);
        dto.setIsPartial(true);

        SourceReference sourceReference = new SourceReference();
        sourceReference.setFormat(format);
        sourceReference.setContent(body);
        dto.setSources(List.of(sourceReference));

        return dto;
    }

    private static void sanitizeTemplates(LandscapeDescription landscapeDescription) {
        //sanitize templates, unset properties which are not reusable
        if (landscapeDescription.getTemplates() != null) {
            landscapeDescription.getTemplates().forEach((s, tpl) -> tpl.setName(""));
        }
    }
}
