package de.bonndan.nivio.input.dto;

import java.io.Serializable;
import java.net.URL;


public class InterfaceDescription implements Serializable {

    private String description;
    private String format;
    private URL url;
    private String protection;
    private Boolean deprecated;
    private String name;
    private String payload;
    private String path;
    private String summary;
    private String parameters;

    public InterfaceDescription() {

    }

    public InterfaceDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public URL getUrl() {
        return url;
    }

    public String getProtection() {
        return protection;
    }

    public void setProtection(String protection) {
        this.protection = protection;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getParameters() {
        return parameters;
    }
}
