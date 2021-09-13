package de.bonndan.nivio.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.model.AbstractLink;
import de.bonndan.nivio.util.URLFactory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is a reference to an input file.
 */
@Schema(description = "This is a reference to a configuration file.")
public class SourceReference extends AbstractLink {

    @Schema(hidden = true)
    private SeedConfiguration config;

    @Schema(description = "The input format.", allowableValues = {"nivio", "csv", "k8s", "rancher", "docker-compose-v2"})
    private String format;

    @Schema(description = "A map with template identifier as key and item identifier matchers as value", example = "endOfLife: [web, \"java6*\"]")
    private Map<String, List<String>> assignTemplates = new HashMap<>();

    /**
     * String arg factory for jackson
     */
    @JsonCreator
    public static SourceReference create(String url) {
        return new SourceReference(URLFactory.getURL(url).orElse(null));
    }

    public SourceReference() {
        super(null);
    }

    /**
     * Constructor for deserialization.
     *
     * @param href path, url or partial path.
     */

    public SourceReference(URL href) {
        super(href);
    }

    public SourceReference(URL url, String format) {
        this(url);
        this.format = format;
    }

    public URL getUrl() {
        return getHref();
    }

    @JsonDeserialize(using = URLFactory.URLDeserializer.class)
    public void setUrl(URL url) {
        this.setHref(url);
    }

    /**
     * Ensures that relative paths are resolved to complete URLs.
     *
     * @throws ReadingException if an URL cannot be determined
     */
    public void setConfig(SeedConfiguration config) {
        this.config = config;
        Optional<String> original = URLFactory.getOriginalRelativePath(this.getUrl());
        original.ifPresent(originalRelativePath -> {
            if (config.getBaseUrl() == null) {
                throw new ProcessingException(String.format("Config has no base url, cannot resolve relative path %s", originalRelativePath));
            }

            try {
                URL url = new URL(URLFactory.combine(config.getBaseUrl(), originalRelativePath));
                this.setUrl(url);
            } catch (MalformedURLException e) {
                throw new ReadingException(String.format("Cannot construct source url of parts %s and %s", config.getBaseUrl(), getUrl()), e);
            }
        });
    }

    /**
     * see {@link de.bonndan.nivio.input.InputFormatHandler}
     *
     * @return the input format type
     */
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public SeedConfiguration getSeedConfig() {
        return config;
    }

    public Map<String, List<String>> getAssignTemplates() {
        return assignTemplates;
    }

    public void setAssignTemplates(Map<String, List<String>> assignTemplates) {
        this.assignTemplates = assignTemplates;
    }
}
