package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.util.Mappers;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class EnvironmentFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentFactory.class);
    private static final ObjectMapper mapper = Mappers.gracefulYamlMapper;

    public static LandscapeDescription fromYaml(File file) {

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            if (StringUtils.isEmpty(content)) {
                LOGGER.warn("Got a seemingly empty file " + file + ". Skipping"); //TODO watcher issue
                return null;
            }
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
     * @param yaml source
     * @param origin origin of the yaml for debugging
     * @return environment description
     */
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
    public static LandscapeDescription fromString(String yaml, URL url) {
        LandscapeDescription env = fromString(yaml, url.toString());
        env.setSource(url.toString());
        return env;
    }

    private static void sanitizeTemplates(LandscapeDescription landscapeDescription) {
        //sanitize templates, unset properties which are not reusable
        if (landscapeDescription.getTemplates() != null) {
            landscapeDescription.getTemplates().forEach(tpl -> {
                tpl.setName("");
                tpl.setShortName("");
            });
        }
    }
}
