package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.landscape.InterfaceItem;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class InterfaceDescription implements InterfaceItem, Serializable {

    private String description;
    private String format;
    private String protocol;
    private Integer port;

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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
