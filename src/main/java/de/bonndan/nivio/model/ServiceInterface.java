package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.bonndan.nivio.input.dto.InterfaceDescription;

import java.net.URL;

public class ServiceInterface {

    @JsonBackReference
    private Item item;

    private String description;
    private String format;
    private URL url;
    private String protection;

    public ServiceInterface(InterfaceDescription interfaceItem) {
        this.description = interfaceItem.getDescription();
        this.format = interfaceItem.getFormat();
        this.url = interfaceItem.getUrl();
    }

    public String getDescription() {
        return description;
    }

    public String getFormat() {
        return format;
    }

    public URL getUrl() {
        return url;
    }

    public String getProtection() {
        return protection;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setProtection(String protection) {
        this.protection = protection;
    }
}
