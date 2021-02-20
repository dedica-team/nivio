package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.*;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.ItemRelationProcessor;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.net.URI;
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
    private final Landscape landscape;

    private final String name;

    private final String owner;

    private final String contact;

    private final String description;

    private final String group;

    private final String color;

    /**
     * technical address
     */
    private URI address;

    private final Map<String, Link> links = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();

    /**
     * Can be both read and modified by {@link ItemRelationProcessor}
     */
    @JsonManagedReference
    private final Set<Relation> relations = ConcurrentHashMap.newKeySet();

    @JsonManagedReference
    private Set<ServiceInterface> interfaces = new HashSet<>();

    public Item(@NotNull String identifier,
                @NotNull Landscape landscape,
                @NotNull String group,
                String name,
                String owner,
                String contact,
                String description,
                String color,
                String icon,
                URI address
    ) {
        if (StringUtils.isEmpty(identifier)) {
            throw new RuntimeException("Identifier must not be empty");
        }
        this.identifier = identifier.toLowerCase();

        this.landscape = Objects.requireNonNull(landscape, "Landscape must not be null");
        if (StringUtils.isEmpty(group)) {
            throw new RuntimeException("Group identifier must not be empty");
        }
        this.group = group;

        this.name = name;
        this.owner = owner;
        this.contact = contact;
        this.description = description;
        this.color = color;
        this.setLabel(Label.icon, icon);
        this.address = address;
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

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String getIcon() {
        return getLabel(Label.icon);
    }

    @Override
    public String getColor() {
        return color;
    }

    public String getContact() {
        return contact;
    }

    @Schema(name = "_links")
    public Map<String, Link> getLinks() {
        return links;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public String getDescription() {
        return description;
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
        return Labeled.withoutKeys(labels, Label.condition.name(), Label.status.name(), Tagged.LABEL_PREFIX_TAG, Label.type.name(), Label.icon.name());
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

    public String getType() {
        return getLabel(Label.type);
    }

    public String getAddress() {
        return address != null ? address.toString() : null;
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
