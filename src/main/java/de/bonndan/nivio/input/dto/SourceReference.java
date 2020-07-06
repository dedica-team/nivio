package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a reference to a configuration file.
 *
 * TODO extend {@link de.bonndan.nivio.model.Link} but allow relative paths.
 */
public class SourceReference {

    private String url;

    private LandscapeDescription landscapeDescription;

    private String format;

    private String basicAuthUsername;
    private String basicAuthPassword;

    private String headerTokenName;
    private String headerTokenValue;

    private Map<String, List<String>> assignTemplates = new HashMap<>();
    private String content;

    private Map<String, Object> props = new HashMap<>();

    public SourceReference() {
    }

    public SourceReference(String url) {
        this.url = url;
    }

    public SourceReference(String url, String format) {
        this.url = url;
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLandscapeDescription(LandscapeDescription landscapeDescription) {
        this.landscapeDescription = landscapeDescription;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public LandscapeDescription getLandscapeDescription() {
        return landscapeDescription;
    }

    public boolean hasBasicAuth() {
        return !StringUtils.isEmpty(basicAuthUsername) && !StringUtils.isEmpty(basicAuthPassword);
    }

    public String getBasicAuthUsername() {
        return basicAuthUsername;
    }

    public void setBasicAuthUsername(String basicAuthUsername) {
        this.basicAuthUsername = basicAuthUsername;
    }

    public String getBasicAuthPassword() {
        return basicAuthPassword;
    }

    public void setBasicAuthPassword(String basicAuthPassword) {
        this.basicAuthPassword = basicAuthPassword;
    }

    public boolean hasHeaderToken() {
        return !StringUtils.isEmpty(headerTokenName) && !StringUtils.isEmpty(headerTokenValue);
    }

    public String getHeaderTokenName() {
        return headerTokenName;
    }

    public void setHeaderTokenName(String headerTokenName) {
        this.headerTokenName = headerTokenName;
    }

    public String getHeaderTokenValue() {
        return headerTokenValue;
    }

    public void setHeaderTokenValue(String headerTokenValue) {
        this.headerTokenValue = headerTokenValue;
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

    @JsonAnyGetter
    public Object getProperty(String key) {
        return props.get(key);
    }

    @JsonAnySetter
    public void setProperty(String key, Object value) {
        props.put(key, value);
    }
}
