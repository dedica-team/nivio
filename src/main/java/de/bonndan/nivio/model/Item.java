package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.*;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.ItemRelationProcessor;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "fullyQualifiedIdentifier")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item implements Linked, Tagged, Labeled, Assessable {

    public static final String LAYER_INFRASTRUCTURE = "infrastructure";
    public static final String LAYER_APPLICATION = "applications";
    public static final String LAYER_INGRESS = "ingress";

    public static final String IDENTIFIER_VALIDATION = "^[a-zA-Z0-9\\.\\:_-]{2,256}$";

    @NotNull
    @Pattern(regexp = IDENTIFIER_VALIDATION)
    private final String identifier;

    @NotNull
    @JsonIgnore
    @Schema(hidden = true)
    private Landscape landscape;

    private String name;

    private String owner;

    private String contact;

    private final Map<String, Link> links = new HashMap<>();

    private String description;

    private String group;

    /**
     * Can be both read and modified by {@link ItemRelationProcessor}
     */
    @JsonManagedReference
    private final Set<Relation> relations = ConcurrentHashMap.newKeySet();

    @JsonManagedReference
    private Set<ServiceInterface> interfaces = new HashSet<>();

    private Map<String, String> labels = new HashMap<>();
    private String color;
    private String icon;

    public Item(String group, String identifier) {
        this.group = group;
        if (StringUtils.isEmpty(identifier)) {
            throw new RuntimeException("Identifier must not be empty");
        }
        this.identifier = identifier.toLowerCase();
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(landscape == null ? "" : landscape.getIdentifier(), group, identifier);
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public void setLandscape(Landscape landscape) {
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

    /**
     * Sets the group name IF not set previously.
     *
     * @param group name
     */
    void setGroup(String group) {
        if (this.group != null && group != null && !this.group.equals(group)) {
            throw new IllegalArgumentException(String.format("A once set item group ('%s') cannot be overwritten with ('%s').", this.group, group));
        }
        this.group = group;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * Returns the labels without the internal ones (having prefixes).
     *
     * @return filtered labels
     */
    @JsonProperty("labels")
    public Map<String, String> getJSONLabels() {

        return Labeled.groupedByPrefixes(
                Labeled.withoutPrefixes(labels, Label.condition.name(), Label.status.name(), Tagged.LABEL_PREFIX_TAG),
                ","
        );
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @JsonIgnore
    public Set<Relation> getRelations() {
        return relations;
    }

    @JsonProperty("relations")
    public Map<String, Relation.ApiModel> getJSONRelations() {
        Map<String, Relation.ApiModel> map = new HashMap<>();

        relations.forEach(relation -> {
            Relation.ApiModel apiModel = new Relation.ApiModel(relation, this);
            map.put(apiModel.id, apiModel);
        });

        return map;
    }

    public Set<Relation> getRelations(RelationType type) {
        return relations.stream()
                .filter(relationItem -> type.equals(relationItem.getType()))
                .collect(Collectors.toSet());
    }

    public void setRelations(Set<Relation> outgoing) {
        relations.addAll(outgoing);
    }

    public void setType(String type) {
        this.setLabel(Label.type, type);
    }

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
                .map(Relation::getSource)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void setInterfaces(Set<ServiceInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<ServiceInterface> getInterfaces() {
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

        if (!(o instanceof Item))
            return false;

        Item item = (Item) o;
        if (toString() == null)
            return false;

        return toString().equals(item.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
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
