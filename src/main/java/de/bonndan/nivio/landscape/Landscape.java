package de.bonndan.nivio.landscape;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 *
 */
@Entity
@Table(name = "landscapes")
public class Landscape implements LandscapeItem {

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    @Id
    @Pattern(regexp = ServiceItem.IDENTIFIER_VALIDATION)
    private String identifier;

    /**
     * Human readable name.
     */
    private String name;

    /**
     * Maintainer email
     */
    private String contact;

    @Column(columnDefinition = "TEXT")
    private String source;

    /**
     * List of configuration services.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "landscape")
    @JsonManagedReference
    private List<Service> services = new ArrayList<>();

    @ElementCollection(targetClass = StateProviderConfig.class)
    private List<StateProviderConfig> stateProviders;

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getContact() {
        return contact;
    }

    @Override
    public List<StateProviderConfig> getStateProviders() {
        return stateProviders;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public void setStateProviders(List<StateProviderConfig> stateProviders) {
        this.stateProviders = stateProviders;
    }
}
