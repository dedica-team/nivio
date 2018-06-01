package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ServiceDescription;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Configures an input.
 *
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 *
 * This is persisted in a H2 db just for keeping track of the environments.
 */
@Entity
@Table(name = "environments")
public class Environment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

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
    @OneToMany
    private List<Source> sources = new ArrayList<>();

    @Transient
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

    public void addService(ServiceDescription serviceDescription) {
        serviceDescription.setEnvironment(this.identifier);
        serviceDescription.getInfrastructure().forEach(s -> s.setEnvironment(this.identifier));
        serviceDescriptions.add(serviceDescription);
    }

    public List<ServiceDescription> getServiceDescriptions() {
        return serviceDescriptions;
    }
}
