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
}
