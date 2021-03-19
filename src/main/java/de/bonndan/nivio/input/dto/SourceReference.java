package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.Link;
import de.bonndan.nivio.util.URLHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a reference to a configuration file.
 */
@Schema(description = "This is a reference to a configuration file.")
public class SourceReference extends Link {

    /**
     * used since Link cannot use relative paths as href
     */
    @Schema(description = "A URL, but can also be a relative path", example = "./a/items.yaml")
    private String url;

    @Schema(hidden = true)
    private LandscapeDescription landscapeDescription;

    @Schema(description = "The input format.", allowableValues = {"nivio", "csv", "k8s", "rancher", "docker-compose-v2"})
    private String format;

    @Schema(description = "A map with template identifier as key and item identifier matchers as value", example = "endOfLife: [web, \"java6*\"]")
    private Map<String, List<String>> assignTemplates = new HashMap<>();

    @Schema(hidden = true)
    private String content;

    /**
     * For testing.
     */
    public static SourceReference of(File file) {
        return new SourceReference(file.toPath().toString());
    }

    /**
     * Constructor for deserialization.
     */
    public SourceReference() {
        super("");
    }

    /**
     * Constructor for deserialization.
     *
     * @param href path, url or partial path.
     */
    public SourceReference(String href) {
        super(URLHelper.getURL(href).map(URL::toString).orElse(""));
        if (StringUtils.isEmpty(this.getHref())) {
            this.url = href;
        }
    }

    public SourceReference(String url, String format) {
        super(url);
        this.format = format;
    }

    public String getUrl() {
        if (this.url != null)
            return url;
        return getHref().toString();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLandscapeDescription(LandscapeDescription landscapeDescription) {
        this.landscapeDescription = landscapeDescription;
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

    public LandscapeDescription getLandscapeDescription() {
        return landscapeDescription;
    }

    public Map<String, List<String>> getAssignTemplates() {
        return assignTemplates;
    }

    public void setAssignTemplates(Map<String, List<String>> assignTemplates) {
        this.assignTemplates = assignTemplates;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * A source reference has content in case of http api pushes.
     */
    public String getContent() {
        return content;
    }
}
