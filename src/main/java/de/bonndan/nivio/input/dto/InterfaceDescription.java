package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.InterfaceItem;

import java.io.Serializable;
import java.net.URL;


public class InterfaceDescription implements Serializable {

    private String description;
    private String format;
    private URL url;
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
