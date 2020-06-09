package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.util.Mappers;
import de.bonndan.nivio.util.URLHelper;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

/**
 * A static factory to create LandscapeDescription instances from files or strings.
 */
@Component
public class LandscapeDescriptionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeDescriptionFactory.class);
    private static final ObjectMapper mapper = Mappers.gracefulYamlMapper;

    private final ApplicationEventPublisher publisher;
    private final FileFetcher fileFetcher;

    public LandscapeDescriptionFactory(ApplicationEventPublisher publisher, FileFetcher fileFetcher) {
        this.publisher = publisher;
        this.fileFetcher = fileFetcher;
    }

    /**
     * Returns a {@link LandscapeDescription}s from config file url.
     *
     * @param old an outdated landscape / description
     * @return the description or null if the source is no URL
     */
    @Nullable
    public LandscapeDescription from(Landscape old) {
        try {
            URL url = new URL(old.getSource());
            return from(url);
        } catch (MalformedURLException e) {
            String msg = "Source in landscape " + old.getIdentifier() + " might be no url: " + old.getSource();
            LOGGER.info(msg);
            return null;
        }
    }

    @Nullable
    public LandscapeDescription from(URL url) {
        LandscapeDescription env = null;
        try {
            if (URLHelper.isLocal(url)) {
                File file = Paths.get(url.toURI()).toFile();
                env = LandscapeDescriptionFactory.fromYaml(file);
                env.setSource(url.toString());
            } else {
                env = LandscapeDescriptionFactory.fromString(fileFetcher.get(url), url);
            }

            LOGGER.info("Created file map for landscape {}", env.getIdentifier());
        } catch (URISyntaxException e) {
            ProcessingException ex = new ProcessingException("Failed to initialize url  " + url, e);
            publisher.publishEvent(new ProcessingErrorEvent(this, ex));
        }
        return env;
    }

    /**
     * @param file yaml file object
     * @return the descriptions or throws
     * @throws ReadingException on error
     */
    @NonNull
    public static LandscapeDescription fromYaml(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            LandscapeDescription landscapeDescription = fromString(content, file.toString());
            landscapeDescription.setSource(file.toString());
            landscapeDescription.getSourceReferences().forEach(ref -> ref.setLandscapeDescription(landscapeDescription));
            return landscapeDescription;
        } catch (NoSuchFileException e) {
            throw new ReadingException("Could not find file " + file.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new ReadingException("Failed to create an environment from file " + file.getAbsolutePath(), e);
        }
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
