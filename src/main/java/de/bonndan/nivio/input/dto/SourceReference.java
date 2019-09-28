package de.bonndan.nivio.input.dto;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceReference {

    private String url;

    private Environment environment;

    private SourceFormat format;

    private String basicAuthUsername;
    private String basicAuthPassword;

    private String headerTokenName;
    private String headerTokenValue;

    private Map<String, List<String>> assignTemplates = new HashMap<>();
    private String content;

    public SourceReference() {
    }

    public SourceReference(String url) {
        this.url = url;
    }

    public SourceReference(SourceFormat format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public SourceFormat getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = SourceFormat.from(format);
    }

    public void setFormat(SourceFormat format) {
        this.format = format;
    }

    public Environment getEnvironment() {
        return environment;
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
}
