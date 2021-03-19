package de.bonndan.nivio.input.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.net.URL;

@Schema(description = "Describes a low-level interface of an item.")
public class InterfaceDescription implements Serializable {

    @Schema(description = "A brief description.")
    private String description;

    @Schema(description = "The payload format.")
    private String format;

    @Schema(description = "A URL describing the endpoint.")
    private URL url;

    @Schema(description = "A description of the interface protection method.")
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
