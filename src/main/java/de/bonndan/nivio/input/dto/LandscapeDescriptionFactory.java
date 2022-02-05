package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.Mappers;
import de.bonndan.nivio.input.ReadingException;
import de.bonndan.nivio.input.SeedConfiguration;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.apache.commons.codec.digest.DigestUtils.md5;

/**
 * A factory to create Landscape DTO instances.
 */
@Component
public class LandscapeDescriptionFactory {

    private static final ObjectMapper mapper = Mappers.gracefulYamlMapper;

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
            var config = mapper.readValue(yaml, SeedConfiguration.class);
            LandscapeDescription landscapeDescription = createDefaultDTO(config);
            landscapeDescription.setSource(new Source(yaml));
            return landscapeDescription;
        } catch (JsonMappingException e) {
            throw ReadingException.fromMappingException(origin, e);
        } catch (IOException e) {
            throw new ReadingException("Failed to create an environment from yaml input string: " + e.getMessage(), e);
        }
    }

    @NonNull
    public static LandscapeDescription createDefaultDTO(@NonNull final SeedConfiguration seedConfiguration) {

        Objects.requireNonNull(seedConfiguration, "A seed config must be provided.");
        if (!StringUtils.hasLength(seedConfiguration.getIdentifier())) {
            throw new IllegalArgumentException("Seed config does not have an identifier to create a default landscape description.");
        }
        String identifier = StringUtils.hasLength(seedConfiguration.getIdentifier()) ?
                seedConfiguration.getIdentifier() :
                new String(md5(seedConfiguration.getSource().toString().getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        LandscapeDescription landscapeDescription = new LandscapeDescription(
                identifier,
                seedConfiguration.getName(),
                seedConfiguration.getContact(),
                seedConfiguration.getDescription(),
                seedConfiguration.getUnits(),
                seedConfiguration.getContexts(),
                seedConfiguration.getGroups(),
                seedConfiguration.getItems()
        );

        landscapeDescription.setLabels(seedConfiguration.getLabels());
        if (seedConfiguration.getTemplates() != null) {
            landscapeDescription.setTemplates(seedConfiguration.getTemplates());
        }
        landscapeDescription.setConfig(seedConfiguration.getConfig());
        return landscapeDescription;

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
        env.setSource(new Source(url));
        return env;
    }

    @NonNull
    public LandscapeDescription fromBodyItems(String identifier, String body) {
        LandscapeDescription dto = new LandscapeDescription(identifier);
        dto.setIsPartial(true);
        dto.setSource(new Source(body));
        return dto;
    }

}
