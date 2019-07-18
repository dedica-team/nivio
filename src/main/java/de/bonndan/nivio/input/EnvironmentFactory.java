package de.bonndan.nivio.input;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.bonndan.nivio.input.dto.Environment;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class EnvironmentFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentFactory.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public static Environment fromYaml(File file) {

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            if (StringUtils.isEmpty(content)) {
                LOGGER.warn("Got a seemingly empty file " + file + ". Skipping"); //TODO watcher issue
                return null;
            }
            Environment environment = fromString(content);
            environment.setSource(file.toString());
            environment.getSourceReferences().forEach(ref -> ref.setEnvironment(environment));
            return environment;
        } catch (IOException e) {
            throw new ReadingException("Failed to create an environment from file " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Creates a new environment description and sets the given yaml as source.
     *
     * @param yaml source
     * @return environment description
     */
    public static Environment fromString(String yaml) {

        yaml = (new StringSubstitutor(StringLookupFactory.INSTANCE.environmentVariableStringLookup())).replace(yaml);

        try {
            Environment environment = mapper.readValue(yaml, Environment.class);
            environment.setSource(yaml);
            environment.getSourceReferences().forEach(ref -> ref.setEnvironment(environment));
            sanitizeTemplates(environment);
            return environment;
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
    public static Environment fromString(String yaml, URL url) {
        Environment env = fromString(yaml);
        env.setSource(url.toString());
        return env;
    }

    private static void sanitizeTemplates(Environment environment) {
        //sanitize templates, unset properties which are not reusable
        if (environment.getTemplates() != null) {
            environment.getTemplates().forEach(tpl -> {
                tpl.setName("");
                tpl.setShort_name("");
            });
        }
    }
}
