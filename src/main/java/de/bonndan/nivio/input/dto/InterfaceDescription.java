package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.landscape.InterfaceItem;

import java.io.Serializable;
import java.net.URL;


public class InterfaceDescription implements InterfaceItem, Serializable {

    private String description;
    private String format;
    private URL url;

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

    @Override
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
