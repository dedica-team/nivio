package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.bonndan.nivio.input.dto.InterfaceDescription;

import java.net.URL;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceInterface {

    private final String description;
    private final String format;
    private final URL url;
    private final String protection;
    private final boolean deprecated;
    private final String name;
    private final String payload;
    private final String path;
    private final String summary;
    private final String parameters;

    public ServiceInterface(InterfaceDescription interfaceItem) {
        description = interfaceItem.getDescription();
        format = interfaceItem.getFormat();
        url = interfaceItem.getUrl();
        deprecated = interfaceItem.getDeprecated() != null && interfaceItem.getDeprecated();
        name = interfaceItem.getName();
        payload = interfaceItem.getPayload();
        path = interfaceItem.getPath();
        summary = interfaceItem.getSummary();
        parameters = interfaceItem.getParameters();
        protection = interfaceItem.getProtection();
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

    public boolean isDeprecated() {
        return deprecated;
    }

    public String getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getPayload() {
        return payload;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "ServiceInterface{" +
                "description='" + description + '\'' +
                ", format='" + format + '\'' +
                ", url=" + url +
                ", name='" + name + '\'' +
                '}';
    }
}
