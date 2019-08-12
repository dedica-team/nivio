package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.input.ServiceDescriptionFactory;
import de.bonndan.nivio.landscape.*;

import java.io.File;
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

    private List<ServiceDescription> templates;

    private String source;

    /**
     * List of configuration sources.
     */
    private List<SourceReference> sources = new ArrayList<>();

    /**
     * descriptions of services fetched and parsed from sources
     */
    private List<ServiceDescription> serviceDescriptions = new ArrayList<>();

    private List<StateProviderConfig> stateProviders;

    private LandscapeConfig config;

    private boolean isPartial = false;

    public void setIsPartial(boolean isPartial) {
        this.isPartial = isPartial;
    }

    /**
     * flags that the environment is not complete, but an update
     */
    public boolean isPartial() {
        return isPartial;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setServiceDescriptions(List<ServiceDescription> serviceDescriptions) {
        this.serviceDescriptions = serviceDescriptions;
    }

    public List<ServiceDescription> getServiceDescriptions() {
        return serviceDescriptions;
    }

    public List<ServiceDescription> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ServiceDescription> templates) {
        this.templates = templates;
    }

    public Landscape toLandscape() {
        Landscape landscape = new Landscape();
        landscape.setIdentifier(identifier);
        landscape.setName(name);
        landscape.setContact(contact);
        landscape.setSource(source);
        landscape.setStateProviders(stateProviders);
        landscape.setConfig(config);
        return landscape;
    }

    public void addServices(List<ServiceDescription> incoming) {
        if (incoming == null)
            return;

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

    /**
     * For compatibility with source references, service can be added directly to the env description.
     */
    public void setServices(List<ServiceDescription> services) {
        addServices(services);
    }

    @Override
    public String toString() {
        return identifier;
    }

    public boolean hasReference(String source) {
        return sources.stream().anyMatch(sourceReference -> {

            if (sourceReference.getUrl().equals(source))
                return true;

            File file = new File(source);
            if (sourceReference.getUrl().contains(file.getName())) //TODO
                return true;

            return false;
        });
    }

    @Override
    public LandscapeConfig getConfig() {
        return config;
    }
}
