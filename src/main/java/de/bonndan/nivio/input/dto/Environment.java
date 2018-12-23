package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.input.ServiceDescriptionFactory;
import de.bonndan.nivio.landscape.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Configures an input.
 * <p>
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 */
public class Environment implements LandscapeItem {

    public static final Environment NONE = new Environment();

    static {
        NONE.contact = "";
        NONE.identifier = "unknown landscape";
    }

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    private String identifier;

    /**
     * Human readable name.
     */
    private String name;

    /**
     * Contact of the maintainer
     */
    private String contact;

    private String path;

    /**
     * List of configuration sources.
     */
    private List<SourceReference> sources = new ArrayList<>();

    private List<ServiceDescription> serviceDescriptions = new ArrayList<>();

    private List<StateProviderConfig> stateProviders;

    /**
     * flags that the environment is not complete, but an update
     */
    private boolean isIncrement = false;

    public void setIsIncrement(boolean isIncrement) {
        this.isIncrement = isIncrement;
    }

    public boolean isIncrement() {
        return isIncrement;
    }

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

    public String getContact() {
        return contact;
    }

    @Override
    public List<StateProviderConfig> getStateProviders() {
        return stateProviders;
    }

    public void setStateProviders(List<StateProviderConfig> stateProviders) {
        this.stateProviders = stateProviders;
    }

    public List<SourceReference> getSourceReferences() {
        return sources;
    }

    public void setSources(List<SourceReference> sources) {
        this.sources = sources;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setServiceDescriptions(List<ServiceDescription> serviceDescriptions) {
        this.serviceDescriptions = serviceDescriptions;
    }

    public List<ServiceDescription> getServiceDescriptions() {
        return serviceDescriptions;
    }

    public Landscape toLandscape() {
        Landscape landscape = new Landscape();
        landscape.setIdentifier(identifier);
        landscape.setName(name);
        landscape.setContact(contact);
        landscape.setPath(path);
        landscape.setStateProviders(stateProviders);
        return landscape;
    }

    public void addServices(List<ServiceDescription> incoming) {
        incoming.forEach(desc -> {
            desc.setEnvironment(this.identifier);

            ServiceDescription existing = (ServiceDescription) ServiceItems.find(desc.getIdentifier(), desc.getGroup(), serviceDescriptions);
            if (existing != null) {
                ServiceDescriptionFactory.assignNotNull(existing, desc);
            } else {
                this.serviceDescriptions.add(desc);
            }
        });
    }

    @Override
    public String toString() {
        return identifier;
    }
}
