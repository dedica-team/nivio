package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.input.ItemDescriptionValues;
import de.bonndan.nivio.input.ItemDescriptions;
import de.bonndan.nivio.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Configures an input.
 * <p>
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 */
@JsonIgnoreType
public class LandscapeDescription implements Component, Linked {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeDescription.class);

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
    private String description;
    private String owner;

    private Map<String, ItemDescription> templates = new HashMap<>();

    private String source;

    /**
     * List of configuration sources.
     */
    private List<SourceReference> sources = new ArrayList<>();

    /**
     * descriptions of items fetched and parsed from sources
     */
    private final ItemDescriptions itemDescriptions = new ItemDescriptions();

    private final LandscapeConfig config = new LandscapeConfig();

    private boolean isPartial = false;

    private Map<String, GroupDescription> groups = new HashMap<>();
    private final Map<String, Link> links = new HashMap<>();
    private Map<String, String> labels = new HashMap<>();

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

    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(identifier, null, null);
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

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public String getIcon() {
        return null;
    }

    public String getColor() {
        return null;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
        this.itemDescriptions.set(itemDescriptions);
    }

    public ItemDescriptions getItemDescriptions() {
        return itemDescriptions;
    }

    public Map<String, ItemDescription> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, ItemDescription> templates) {
        templates.forEach((s, itemDescription) -> itemDescription.setIdentifier(s));
        this.templates = templates;
    }

    public void addItems(Collection<ItemDescription> incoming) {
        if (incoming == null)
            return;

        incoming.forEach(desc -> {
            desc.setEnvironment(this.identifier);

            ItemDescription existing = itemDescriptions.find(desc.getIdentifier(), desc.getGroup()).orElse(null);
            if (existing != null) {
                ItemDescriptionValues.assignNotNull(existing, desc);
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

    public LandscapeConfig getConfig() {
        return config;
    }

    public Map<String, GroupDescription> getGroups() {
        return groups;
    }

    /**
     * Manually set Identifiers are overridden by keys.
     *
     * @param groups the configured groups
     */
    @JsonDeserialize(contentAs = GroupDescription.class)
    public void setGroups(Map<String, GroupDescription> groups) {

        groups.forEach((s, groupItem) -> {
            if (!s.equals(groupItem.getIdentifier()) && !StringUtils.isEmpty(groupItem.getIdentifier())) {
                LOGGER.warn("Group map key {} and identifier {} are both set and differ. Overriding with map key.", s, groupItem.getIdentifier());
            }
            groupItem.setIdentifier(s);
            groupItem.setEnvironment(identifier);
        });
        this.groups = groups;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }
}
