package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ComponentMatcher;
import de.bonndan.nivio.search.LuceneSearchIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.*;

/**
 * Input DTO for a {@link Landscape}.
 *
 * Think of a group of servers and apps, like a "project", "workspace" or stage.
 */
@JsonIgnoreType
public class LandscapeDescription extends ComponentDescription {

    @Schema(description = "Item descriptions to be used as templates. All values except identifier and name will be applied to the assigned items.")
    private Map<String, ItemDescription> templates = new HashMap<>();

    @Schema(hidden = true)
    private Source source;

    @Schema(description = "Configuration of key performance indicators (i.e. status indicators) and layouting tweaks.")
    private LandscapeConfig config = new LandscapeConfig();

    @Schema(hidden = true)
    private boolean isPartial = false;

    /**
     * in-memory index of landscape components
     */
    @Schema(hidden = true)
    private final Index<ComponentDescription> index = new Index<>(LuceneSearchIndex.createVolatile());

    private final Map<String, List<String>> assignTemplates = new HashMap<>();

    @JsonCreator
    public LandscapeDescription(@NonNull final String identifier) {
        this.setIdentifier(identifier);
    }

    @JsonCreator
    public LandscapeDescription(@JsonProperty("identifier") @NonNull String identifier,
                                @JsonProperty("name") @NonNull String name,
                                @JsonProperty("contact") @Nullable String contact
    ) {
        this(identifier);
        this.setName(Objects.requireNonNull(name));
        this.setContact(contact);
    }

    public LandscapeDescription(String identifier,
                                String name,
                                String contact,
                                String description,
                                List<UnitDescription> units,
                                List<ContextDescription> contexts,
                                Map<String, GroupDescription> groups,
                                List<ItemDescription> items
    ) {
        this(identifier, name, contact);
        this.setDescription(description);

        units.forEach(index::addOrReplace);
        contexts.forEach(index::addOrReplace);
        groups.forEach((s, groupDescription) -> {
            groupDescription.setIdentifier(s);
            index.addOrReplace(groupDescription);
        });
        items.forEach(index::addOrReplace);
    }

    public void setIsPartial(boolean isPartial) {
        this.isPartial = isPartial;
    }

    @Schema(description = "marks that the landscape is not complete, but an update")
    public boolean isPartial() {
        return isPartial;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public IndexReadAccess<ComponentDescription> getIndexReadAccess() {
        return new IndexReadAccess<>(index);
    }

    @Deprecated
    public Set<ItemDescription> getItemDescriptions() {
        return new IndexReadAccess<>(index).all(ItemDescription.class);
    }


    public IndexWriteAccess<ComponentDescription> getWriteAccess() {
        return new IndexWriteAccess<>(index);
    }

    /**
     * Merges the incoming items with existing ones.
     *
     * Already existing ones are updated.
     *
     * @param incoming new data
     */
    public void mergeItems(@Nullable Collection<ItemDescription> incoming) {
        if (incoming == null) {
            return;
        }

        incoming.forEach(desc -> {
            Optional<ItemDescription> existing = getIndexReadAccess().findOneMatching(
                    ComponentMatcher.buildForItemAndGroup(desc.getIdentifier(), desc.getGroup()),
                    ItemDescription.class
            );
            if (existing.isPresent()) {
                existing.get().assignNotNull(desc);
            } else {
                this.index.addOrReplace(desc);
            }
        });
    }

    public void mergeUnits(@Nullable Collection<UnitDescription> incoming) {
        if (incoming == null) {
            return;
        }

        incoming.forEach(desc -> {
            Optional<UnitDescription> existing = getIndexReadAccess().findOneMatching(
                    ComponentMatcher.build(null, desc.getIdentifier(), null, null, null),
                    UnitDescription.class
            );
            if (existing.isPresent()) {
                existing.get().assignNotNull(desc);
            } else {
                this.index.addOrReplace(desc);
            }
        });
    }

    public void mergeContexts(@Nullable Collection<ContextDescription> incoming) {
        if (incoming == null) {
            return;
        }

        incoming.forEach(desc -> {
            Optional<ContextDescription> existing = getIndexReadAccess().findOneMatching(
                    ComponentMatcher.build(null, desc.getUnit(), desc.getIdentifier(), null, null),
                    ContextDescription.class
            );
            if (existing.isPresent()) {
                existing.get().assignNotNull(desc);
            } else {
                this.index.addOrReplace(desc);
            }
        });
    }

    /**
     * Merges the incoming groups with existing ones.
     *
     * Already existing ones are updated.
     *
     * @param incoming new data
     */
    public void mergeGroups(@Nullable Collection<GroupDescription> incoming) {
        if (incoming == null) {
            return;
        }

        incoming.forEach(groupDescription -> {
            Optional<GroupDescription> existing = getIndexReadAccess().findOneByIdentifiers(groupDescription.getIdentifier(), groupDescription.getParentIdentifier(), GroupDescription.class);
            if (existing.isPresent()) {
                existing.get().assignNotNull(groupDescription);
            } else {
                this.index.addOrReplace(groupDescription);
            }
        });
    }

    public LandscapeConfig getConfig() {
        return config;
    }

    public Map<String, ItemDescription> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, ItemDescription> templates) {
        this.templates = templates;
    }

    public Map<String, List<String>> getAssignTemplates() {
        return assignTemplates;
    }

    public void setAssignTemplates(Map<String, List<String>> assignTemplates) {
        this.assignTemplates.putAll(assignTemplates);
    }

    public void setConfig(LandscapeConfig config) {
        this.config = config;
    }

    @NonNull
    @Override
    public URI getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.forDescription(LandscapeDescription.class, getIdentifier(), null, null, null, null, null);
    }

    @Override
    public String getParentIdentifier() {
        return null;
    }
}