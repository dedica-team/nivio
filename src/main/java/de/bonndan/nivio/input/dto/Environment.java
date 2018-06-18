package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.input.Source;
import de.bonndan.nivio.landscape.Landscape;

import java.util.ArrayList;
import java.util.List;

/**
 * Configures an input.
 *
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 *
 */
public class Environment {

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    private String identifier;

    /**
     * Human readable name.
     */
    private String name;

    private String path;

    /**
     * List of configuration sources.
     */
    private List<Source> sources = new ArrayList<>();

    private List<ServiceDescription> serviceDescriptions = new ArrayList<>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addServiceDescription(ServiceDescription serviceDescription) {
        serviceDescription.setEnvironment(this.identifier);
        serviceDescription.getInfrastructure().forEach(s -> s.setEnvironment(this.identifier));
        serviceDescriptions.add(serviceDescription);
    }

    public List<ServiceDescription> getServiceDescriptions() {
        return serviceDescriptions;
    }

    public Landscape toLandscape() {
        Landscape landscape = new Landscape();
        landscape.setIdentifier(identifier);
        landscape.setName(name);
        landscape.setPath(path);
        return landscape;
    }
}
