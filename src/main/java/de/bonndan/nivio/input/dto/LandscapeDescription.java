package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.input.ItemDescriptionFactory;
import de.bonndan.nivio.model.*;

import java.io.File;
import java.util.*;

/**
 * Configures an input.
 * <p>
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 */
public class LandscapeDescription implements Landscape {

    public static final LandscapeDescription NONE = new LandscapeDescription();

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

    private List<ItemDescription> templates;

    private String source;

    /**
     * List of configuration sources.
     */
    private List<SourceReference> sources = new ArrayList<>();

    /**
     * descriptions of items fetched and parsed from sources
     */
    private List<ItemDescription> itemDescriptions = new ArrayList<>();

    private List<StateProviderConfig> stateProviders;

    private LandscapeConfig config;

    private boolean isPartial = false;

    private Map<String, GroupItem> groups = new HashMap<>();

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

    public void setItemDescriptions(List<ItemDescription> itemDescriptions) {
        this.itemDescriptions = itemDescriptions;
    }

    public List<ItemDescription> getItemDescriptions() {
        return itemDescriptions;
    }

    public List<ItemDescription> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ItemDescription> templates) {
        this.templates = templates;
    }

    public LandscapeImpl toLandscape() {
        LandscapeImpl landscape = new LandscapeImpl();
        landscape.setIdentifier(identifier);
        landscape.setName(name);
        landscape.setContact(contact);
        landscape.setSource(source);
        landscape.setStateProviders(stateProviders);
        landscape.setConfig(config);
        return landscape;
    }

    public void addItems(List<ItemDescription> incoming) {
        if (incoming == null)
            return;

        incoming.forEach(desc -> {
            desc.setEnvironment(this.identifier);

            ItemDescription existing = (ItemDescription)
                    ServiceItems.find(desc.getIdentifier(), desc.getGroup(), itemDescriptions).orElse(null);
            if (existing != null) {
                ItemDescriptionFactory.assignNotNull(existing, desc);
            } else {
                this.itemDescriptions.add(desc);
            }
        });
    }

    /**
     * For compatibility with source references, items can be added directly to the env description.
     */
    public void setItems(List<ItemDescription> items) {
        addItems(items);
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

    @Override
    public Map<String, GroupItem> getGroups() {
        return groups;
    }

    @JsonDeserialize(contentAs = GroupDescription.class)
    public void setGroups(Map<String, GroupItem> groups) {
        this.groups = groups;
    }
}
