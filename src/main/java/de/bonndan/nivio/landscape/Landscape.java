package de.bonndan.nivio.landscape;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 *
 */
@Entity
@Table(name = "landscapes")
public class Landscape {

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    @Id
    private String identifier;

    /**
     * Human readable name.
     */
    private String name;

    private String path;

    /**
     * List of configuration services.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Service> services = new ArrayList<>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = StringUtils.trimAllWhitespace(identifier);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addService(Service service) {
        service.setLandscape(this);
        service.getProvidedBy().forEach(s -> s.setLandscape(this));
        services.add(service);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Landscape landscape = (Landscape) o;

        return StringUtils.trimAllWhitespace(identifier).equals(StringUtils.trimAllWhitespace(landscape.identifier));
    }

    @Override
    public int hashCode() {
        return Objects.hash(StringUtils.trimAllWhitespace(identifier));
    }
}
