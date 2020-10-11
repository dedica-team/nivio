package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.util.Mappers;
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
 * A static factory to create LandscapeDescription instances from files or strings.
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
     * Returns a {@link LandscapeDescription}s from config file url.
     *
     * @param outdatedLandscape an outdated landscape
     * @return the description or null if the source is no URL
     */
    @Nullable
    public LandscapeDescription from(LandscapeImpl outdatedLandscape) {
        try {
            URL url = new URL(outdatedLandscape.getSource());
            return from(url);
        } catch (MalformedURLException e) {
            String msg = "Source in landscape " + outdatedLandscape.getIdentifier() + " might be no url: " + outdatedLandscape.getSource();
            LOGGER.info(msg);
            return null;
        }
    }

    /**
     * Returns a {@link LandscapeDescription}s from config file url.
     *
     * @param outdatedDescription an outdated description
     * @return the description or null if the source is no URL
     */
    @Nullable
    public LandscapeDescription from(LandscapeDescription outdatedDescription) {
        try {
            URL url = new URL(outdatedDescription.getSource());
            return from(url);
        } catch (MalformedURLException e) {
            String msg = "Source in landscape " + outdatedDescription.getIdentifier() + " might be no url: " + outdatedDescription.getSource();
            LOGGER.info(msg);
            return null;
        }
    }

    @Nullable
    public LandscapeDescription from(URL url) {
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
        landscapeDescription.setSource(file.toString());
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
    public static LandscapeDescription fromString(String yaml, String origin) {

        if (StringUtils.isEmpty(yaml)) {
            throw new ReadingException("Failed to create an environment from empty yaml input string.", new IllegalArgumentException("Got an empty string."));
        }

        yaml = (new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup())).replace(yaml);

        try {
            LandscapeDescription landscapeDescription = mapper.readValue(yaml, LandscapeDescription.class);
            landscapeDescription.setSource(yaml);
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
    public static LandscapeDescription fromString(String yaml, URL url) {
        LandscapeDescription env = fromString(yaml, url.toString());
        env.setSource(url.toString());
        return env;
    }

    private static void sanitizeTemplates(LandscapeDescription landscapeDescription) {
        //sanitize templates, unset properties which are not reusable
        if (landscapeDescription.getTemplates() != null) {
            landscapeDescription.getTemplates().forEach((s, tpl) -> {
                tpl.setName("");
            });
        }
    }
}
