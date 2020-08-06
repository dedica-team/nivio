package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.*;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "fullyQualifiedIdentifier")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item implements LandscapeItem, Tagged, Labeled, Assessable {

    @NotNull
    @Pattern(regexp = LandscapeItem.IDENTIFIER_VALIDATION)
    private String identifier;

    @NotNull
    @JsonIgnore
    @Schema(hidden = true)
    private LandscapeImpl landscape;

    private String name;

    private String owner;

    private String contact;

    private final Map<String, Link> links = new HashMap<>();

    private String description;

    private String group;

    /**
     * Can be both read and modified by {@link de.bonndan.nivio.input.ItemRelationResolver}
     */
    @JsonManagedReference
    private final Set<RelationItem<Item>> relations = ConcurrentHashMap.newKeySet();

    @JsonManagedReference
    private Set<InterfaceItem> interfaces = new HashSet<>();

    private Map<String, String> labels = new HashMap<>();
    private String color;
    private String icon;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        if (StringUtils.isEmpty(identifier)) {
            throw new RuntimeException("Identifier must not be empty");
        }
        this.identifier = identifier.toLowerCase();
    }

    @Override
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(landscape == null ? "" : landscape.getIdentifier(), group, identifier);
    }

    public LandscapeImpl getLandscape() {
        return landscape;
    }

    public void setLandscape(LandscapeImpl landscape) {
        this.landscape = landscape;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Schema(name = "_links")
    public Map<String, Link> getLinks() {
        return links;
    }

    @JsonIgnore
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @Override
    public Set<RelationItem<Item>> getRelations() {
        return relations;
    }

    public Set<RelationItem<Item>> getRelations(RelationType type) {
        return relations.stream()
                .filter(relationItem -> type.equals(relationItem.getType()))
                .collect(Collectors.toSet());
    }

    public void setRelations(Set<RelationItem<Item>> outgoing) {
        relations.addAll(outgoing);
    }

    public void setType(String type) {
        this.setLabel(Label.type, type);
    }

    @Override
    public String getType() {
        return getLabel(Label.type);
    }

    /**
     * Returns all providers.
     */
    @JsonIgnore
    public Set<Item> getProvidedBy() {
        return getRelations(RelationType.PROVIDER).stream()
                .filter(relationItem -> relationItem.getTarget().equals(this))
                .map(RelationItem::getSource)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void setInterfaces(Set<InterfaceItem> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<InterfaceItem> getInterfaces() {
        return interfaces;
    }

    @Override
    @JsonAnyGetter
    public String getLabel(String key) {
        return labels.get(key);
    }

    @Override
    public Map<String, String> getLabels(String prefix) {
        return Labeled.withPrefix(prefix, labels);
    }

    @Override
    @JsonAnySetter
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;

        if (!(o instanceof LandscapeItem))
            return false;

        LandscapeItem landscapeItem = (LandscapeItem) o;
        if (toString() == null)
            return false;

        return toString().equals(landscapeItem.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    /**
     * @return the fully qualified identifier for this service
     */
    @Override
    public String toString() {
        if (landscape == null) {
            return identifier;
        }

        return getFullyQualifiedIdentifier().toString();
    }

    @Override
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(indexedByPrefix(Label.status));
    }
}
