package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.input.ComponentDescriptionValues;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.input.ItemDescriptionValues;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ItemIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Configures an input.
 * <p>
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 */
@JsonIgnoreType
public class LandscapeDescription implements ComponentDescription {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeDescription.class);

    public static final LandscapeDescription NONE = new LandscapeDescription(
            "unknown landscape", "", ""
    );

    /**
     * Immutable unique identifier. Maybe use an URN.
     */
    @NonNull
    private final String identifier;

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
    private final ItemIndex<ItemDescription> itemDescriptions = new ItemIndex<>(null, ItemDescription.class);

    private final LandscapeConfig config = new LandscapeConfig();

    private boolean isPartial = false;

    private Map<String, GroupDescription> groups = new HashMap<>();
    private final Map<String, Link> links = new HashMap<>();
    private Map<String, String> labels = new HashMap<>();

    @JsonCreator
    public LandscapeDescription(@NonNull String identifier) {
        this.identifier = identifier;
    }

    @JsonCreator
    public LandscapeDescription(@JsonProperty("identifier") @NonNull String identifier,
                                @JsonProperty("name") @NonNull String name,
                                @JsonProperty("contact") @Nullable String contact) {
        this.identifier = Objects.requireNonNull(identifier);
        this.name = Objects.requireNonNull(name);
        this.contact = contact;
    }

    public void setIsPartial(boolean isPartial) {
        this.isPartial = isPartial;
    }

    /**
     * flags that the environment is not complete, but an update
     */
    public boolean isPartial() {
        return isPartial;
    }

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(identifier, null, null);
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getContact() {
        return contact;
    }

    @Override
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

    @JsonIgnore
    @Override
    public String getAddress() {
        return null;
    }

    public ItemIndex<ItemDescription> getItemDescriptions() {
        return itemDescriptions;
    }

    public Map<String, ItemDescription> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, ItemDescription> templates) {
        templates.forEach((s, itemDescription) -> itemDescription.setIdentifier(s));
        this.templates = templates;
    }

    /**
     * Merges the incoming items with existing ones.
     *
     * Already existing ones are updated.
     * @param incoming new data
     */
    public void mergeItems(@Nullable Collection<ItemDescription> incoming) {
        if (incoming == null) {
            return;
        }

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
     * Merges the incoming groups with existing ones.
     *
     * Already existing ones are updated.
     * @param incoming new data
     */
    public void mergeGroups(@Nullable Map<String, GroupDescription> incoming) {
        if (incoming == null) {
            return;
        }

        incoming.forEach( (identifier, groupDescription) -> {
            groupDescription.setEnvironment(this.identifier);

            GroupDescription existing = groups.get(identifier);
            if (existing != null) {
                ComponentDescriptionValues.assignNotNull(existing, groupDescription);
            } else {
                this.groups.put(identifier, groupDescription);
            }
        });
    }

    /**
     * For compatibility with source references, items can be added directly to the env description.
     */
    public void setItems(List<ItemDescription> items) {
        mergeItems(items);
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

    @Override
    public String getLabel(String key) {
        return getLabels().get(key);
    }

    @Override
    public void setLabel(String key, String value) {
        getLabels().put(key, value);
    }
}
