package de.bonndan.nivio.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.input.dto.*;
import de.bonndan.nivio.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.*;

/**
 * Input DTO for a landscape.
 *
 * Preliminary stage of a {@link de.bonndan.nivio.input.dto.LandscapeDescription} with unresolved sources. Opposed to a
 * landscape description is has many {@link SourceReference}s which must be resolved into input data by the
 * {@link SourceReferencesResolver}. A single seed config can result in multiple landscape description dtos.
 */
@JsonIgnoreType
public class SeedConfiguration  {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeedConfiguration.class);

    @NonNull
    @Schema(required = true,
            description = "Immutable unique identifier. Maybe use an URN.",
            pattern = IdentifierValidation.IDENTIFIER_PATTERN)
    private final String identifier;

    @Schema(required = true,
            description = "Human readable name."
    )
    private String name;

    @Schema(description = "Primary contact method, preferably an email address.")
    private String contact;

    @Schema(description = "A brief description of the landscape.")
    private String description;

    @Schema(description = "The business owner (person or team), preferably an email address.")
    private String owner;

    @Schema(description = "Item descriptions to be used as templates. All values except identifier and name will be applied to the assigned items.")
    private Map<String, ItemDescription> templates = new HashMap<>();

    @Schema(description = "List of configuration sources. Handled in the given order, latter extend/overwrite earlier values like items etc.")
    private List<SourceReference> sources = new ArrayList<>();

    @Schema(description = "Configuration of key performance indicators (i.e. status indicators) and layouting tweaks.")
    private final LandscapeConfig config = new LandscapeConfig();

    @Schema(hidden = true)
    private boolean isPartial = false;

    @Schema(description = "Description of item groups (optional, can also be given in sources).")
    private Map<String, GroupDescription> groups = new HashMap<>();

    @Schema(description = "Additional links related to the landscape.")
    private final Map<String, Link> links = new HashMap<>();

    @Schema(description = "Additional labels for the landscape.")
    private final Map<String, String> labels = new HashMap<>();

    @Schema(description = "Description of units (optional, can also be given in sources).")
    private List<UnitDescription> units = new ArrayList<>();

    @Schema(description = "Description of contexts within units (optional, can also be given in sources).")
    private List<ContextDescription> contexts = new ArrayList<>();

    @Schema(description = "Description of items (optional, can also be given in sources).")
    private List<ItemDescription> items = new ArrayList<>();

    private URL baseUrl;
    private Source source;

    @JsonCreator
    public SeedConfiguration(@NonNull String identifier) {
        this.identifier = identifier;
    }

    @JsonCreator
    public SeedConfiguration(@JsonProperty("identifier") @NonNull String identifier,
                             @JsonProperty("name") @NonNull String name,
                             @JsonProperty("contact") @Nullable String contact) {
        this.identifier = Objects.requireNonNull(identifier);
        this.name = Objects.requireNonNull(name);
        this.contact = contact;
    }

    public void setIsPartial(boolean isPartial) {
        this.isPartial = isPartial;
    }

    @Schema(description = "marks that the landscape is not complete, but an update")
    public boolean isPartial() {
        return isPartial;
    }

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setIcon(String icon) {
        this.getLabels().put(Label.icon.name(), icon);
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

    @Schema(hidden = true)
    public List<SourceReference> getSourceReferences() {
        return sources;
    }

    public void setSources(List<SourceReference> sources) {
        this.sources = sources;
    }

    public Map<String, ItemDescription> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, ItemDescription> templates) {
        templates.forEach((s, itemDescription) -> itemDescription.setIdentifier(s));
        this.templates = templates;
    }

    /**
     * For compatibility with source references, items can be added directly to the env description.
     */
    @Schema(name = "items", description = "List of configuration sources. Handled in the given order, latter extend/overwrite earlier values like items etc.")
    public void setItems(List<ItemDescription> items) {
        this.items = items;
    }

    public List<ItemDescription> getItems() {
        return items;
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

    public List<UnitDescription> getUnits() {
        return units;
    }

    public void setUnits(List<UnitDescription> units) {
        this.units = units;
    }

    public List<ContextDescription> getContexts() {
        return contexts;
    }

    public void setContexts(List<ContextDescription> contexts) {
        this.contexts = contexts;
    }

    /**
     * Manually set Identifiers are overridden by keys.
     *
     * @param groups the configured groups
     */
    @JsonDeserialize(contentAs = GroupDescription.class)
    public void setGroups(Map<String, GroupDescription> groups) {

        groups.forEach((s, groupItem) -> {
            if (!s.equals(groupItem.getIdentifier()) && StringUtils.hasLength(groupItem.getIdentifier())) {
                LOGGER.warn("Group map key {} and identifier {} are both set and differ. Overriding with map key.", s, groupItem.getIdentifier());
            }
            groupItem.setIdentifier(s);
        });
        this.groups = groups;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("links") //this override is for DTO documentation, hateoas is not relevant here
    public Map<String, Link> getLinks() {
        return links;
    }

    @NonNull
    public Map<String, String> getLabels() {
        return labels;
    }

    public String getLabel(String key) {
        return getLabels().get(key);
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }
}
