package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.*;
import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.input.ItemRelationProcessor;
import de.bonndan.nivio.output.Color;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.ComponentDiff.*;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "fullyQualifiedIdentifier")
//needed when internal models are serialized for debugging
public class Item implements Linked, Tagged, Labeled, Assessable, ItemComponent {

    @NotNull
    @Pattern(regexp = IdentifierValidation.PATTERN)
    private final String identifier;

    @NotNull
    @JsonIgnore //needed when internal models are serialized for debugging
    private final Landscape landscape;

    private final String name;

    private final String owner;

    private final String contact;

    private final String description;

    @NonNull
    private final String group;

    private final String type;

    private final Layer layer;

    /**
     * technical address
     */
    private final URI address;

    private final Map<String, Link> links = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();

    /**
     * Can be both read and modified by {@link ItemRelationProcessor}
     */
    @JsonManagedReference
    private final Set<Relation> relations = ConcurrentHashMap.newKeySet();

    @JsonManagedReference
    private Set<ServiceInterface> interfaces = new HashSet<>();

    public Item(@NotNull final String identifier,
                @NotNull final Landscape landscape,
                @NotNull final String group,
                final String name,
                final String owner,
                final String contact,
                final String description,
                final String color,
                final String icon,
                final String type,
                final URI address,
                final Layer layer
    ) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalArgumentException("Identifier must not be empty");
        }
        this.identifier = identifier.toLowerCase();

        this.landscape = Objects.requireNonNull(landscape, "Landscape must not be null");
        if (!StringUtils.hasLength(group)) {
            throw new IllegalArgumentException("Group identifier must not be empty");
        }
        this.group = group;
        this.name = name;
        this.owner = owner;
        this.contact = contact;
        this.description = description;
        this.address = address;
        this.type = type;
        this.layer = layer;

        //these are effectively mutable
        this.setLabel(Label.color, Color.safe(color));
        this.setLabel(Label.icon, icon);
    }

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    @NonNull
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(landscape == null ? "" : landscape.getIdentifier(), group, identifier);
    }

    public Landscape getLandscape() {
        return landscape;
    }

    @Override
    @NonNull
    public String getName() {
        return name == null ? "" : name;
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
        return getLabel(Label.color);
    }

    public String getContact() {
        return contact;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    @NonNull
    public String getGroup() {
        return group;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @NonNull
    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * Returns the relations.
     *
     * @return immutable set
     */
    @JsonIgnore //needed for internal debugging
    public Set<Relation> getRelations() {
        return Set.copyOf(relations);
    }

    /**
     * Adds a relation or replaces the similar one.
     * <p>
     * This is necessary because {@link Set} does not replace AND we need to check relation end equality on object level
     * because referenced source or target items will be replaced by new copies.
     *
     * @param relation to add or replace
     */
    public void addOrReplace(@NonNull final Relation relation) {
        if (relation.getSource() != this && relation.getTarget() != this) {
            throw new IllegalArgumentException(String.format("Relation contains no reference to item.%s %s", relation.getIdentifier(), this));
        }

        getSimilar(relation).map(relations::remove);
        relations.add(relation);
    }

    /**
     * @param relation the relation to delete
     * @return true if the relation has been removed or was not present.
     */
    public boolean removeRelation(Relation relation) {
        Optional<Relation> similar = getSimilar(relation);
        return similar.map(relations::remove).orElse(true);
    }

    /**
     * Returns a similar existing relation.
     */
    private Optional<Relation> getSimilar(Relation relation) {
        return getRelations().stream()
                .filter(existing -> relation.getSource().equals(existing.getSource()) && relation.getTarget().equals(existing.getTarget()))
                .findFirst();
    }

    void setRelations(Set<Relation> outgoing) {
        this.relations.clear();
        this.relations.addAll(outgoing);
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address != null ? address.toString() : null;
    }

    public void setInterfaces(Set<ServiceInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<ServiceInterface> getInterfaces() {
        return interfaces;
    }

    @Override
    public String getLabel(String key) {
        return labels.get(key);
    }

    @Override
    public Map<String, String> getLabels(String prefix) {
        return Labeled.withPrefix(prefix, labels);
    }

    @Override
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    @Override
    public String getLayer() {
        if (layer == null) {
            return Layer.domain.name();
        }
        return layer.name();
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
    @NonNull
    public Set<StatusValue> getAdditionalStatusValues() {
        return StatusValue.fromMapping(getAssessmentIdentifier(), indexedByPrefix(Label.status));
    }

    @Override
    @NonNull
    public String getAssessmentIdentifier() {
        return getFullyQualifiedIdentifier().toString();
    }

    @Override
    @NonNull
    public List<? extends Assessable> getChildren() {
        return getRelations().stream().filter(relation -> relation.getSource().equals(this)).collect(Collectors.toList());
    }

    /**
     * Compare on field level against a newer version.
     *
     * @param newer the newer version
     * @return a list of changes if any changes are present
     * @throws IllegalArgumentException if the arg is not comparable
     */
    public List<String> getChanges(final Item newer) {
        if (!newer.equals(this)) {
            throw new IllegalArgumentException(String.format("Cannot compare component %s against %s", newer, this));
        }

        List<String> changes = new ArrayList<>();
        changes.addAll(compareStrings(this.contact, newer.contact, "Contact"));
        changes.addAll(compareStrings(this.description, newer.description, "Description"));
        changes.addAll(compareStrings(this.name, newer.name, "Name"));
        changes.addAll(compareStrings(this.owner, newer.owner, "Owner"));
        changes.addAll(compareStrings(this.type, newer.type, "Type"));
        changes.addAll(compareOptionals(Optional.ofNullable(this.address), Optional.ofNullable(newer.address), "Address"));
        changes.addAll(compareCollections(this.links.keySet(), newer.links.keySet(), "Links"));
        changes.addAll(newer.diff(this));

        List<String> collect = this.interfaces.stream().map(ServiceInterface::toString).collect(Collectors.toList());
        List<String> collect2 = newer.getInterfaces().stream().map(ServiceInterface::toString).collect(Collectors.toList());
        changes.addAll(compareCollections(collect, collect2, "Interfaces"));

        return changes;
    }


}
