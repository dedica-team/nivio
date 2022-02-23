package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.util.*;

import static de.bonndan.nivio.model.IdentifierValidation.getValidIdentifier;
import static de.bonndan.nivio.util.SafeAssign.assignSafeIfAbsent;

/**
 * Base interface for input DTOs, which are mutable objects.
 */
public abstract class ComponentDescription implements Component, Labeled, Linked {

    @Schema(description = "Related links")
    private final Map<String, Link> links = new HashMap<>();

    @Schema(description = "Labels (key-value pairs)")
    private final Map<String, String> labels = new HashMap<>();

    @Schema(required = true, description = "A unique identifier for the group (also used as name). Descriptions are merged based on the identifier.",
            example = "shipping",
            pattern = IdentifierValidation.IDENTIFIER_PATTERN)
    @NotEmpty
    private String identifier;

    @Schema(required = true, description = "A human-readable name")
    private String name;

    @Schema(description = "The business owner of the group.")
    private String owner;

    @Schema(description = "A brief description.")
    private String description;

    @Schema(description = "A contact method, preferably email.")
    private String contact;

    @Schema(description = "The HTML (hexcode only!) color used to draw the group and its items. If no color is given, one is computed.", example = "05ffaa")
    private String color;

    @Schema(description = "The type of the component. A string describing its nature. If no icon is set, the type determines the displayed icon.")
    private String type;

    @Schema(description = "A list of item identifiers or SQL-like queries to easily assign items to this group.", example = "identifier LIKE 'DB1'")
    private List<String> contains = new ArrayList<>();

    @Schema(description = "Relations to other landscape items.")
    @JsonDeserialize(contentAs = RelationDescription.class)
    private final Set<RelationDescription> relations = new HashSet<>();

    /**
     * Overwrites and fields on the existing with values of the increment unless the increment value is null.
     *
     * @param increment new values
     */
    public <T extends ComponentDescription> void assignNotNull(T increment) {

        if (increment.getName() != null) {
            this.setName(increment.getName());
        }

        if (increment.getDescription() != null) {
            this.setDescription(increment.getDescription());
        }

        if (increment.getOwner() != null) {
            this.setOwner(increment.getOwner());
        }

        if (increment.getContact() != null) {
            this.setContact(increment.getContact());
        }

        if (increment.getType() != null) {
            this.setType(increment.getType());
        }
        if (increment.getIcon() != null) {
            this.setIcon(increment.getIcon());
        }

        increment.getLabels().forEach((s, s2) -> {
            if (increment.getLabel(s) != null) {
                this.setLabel(s, s2);
            }
        });

        this.getLinks().putAll(increment.getLinks());
    }

    /**
     * Writes the values of the increment (second object) to the first where first is null/absent.
     *
     * @param increment source
     */
    public void assignSafeNotNull(ComponentDescription increment) {

        assignSafeIfAbsent(increment.getName(), getName(), this::setName);
        assignSafeIfAbsent(increment.getDescription(), getDescription(), this::setDescription);
        assignSafeIfAbsent(increment.getOwner(), getOwner(), this::setOwner);
        assignSafeIfAbsent(increment.getContact(), getContact(), this::setContact);
        assignSafeIfAbsent(increment.getType(), getType(), this::setType);

        Labeled.add(increment, this);

        increment.getLinks().entrySet().stream()
                .filter(entry -> !getLinks().containsKey(entry.getKey()))
                .forEach(entry -> getLinks().put(entry.getKey(), entry.getValue()));
    }

    public List<String> getContains() {
        return contains;
    }

    public void setContains(List<String> contains) {
        this.contains = contains;
    }


    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = getValidIdentifier(identifier);
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

    @JsonIgnore
    public String getIcon() {
        return getLabel(Label.icon);
    }

    public void setIcon(String icon) {
        setLabel(Label.icon, icon);
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @JsonProperty("links") //this override is for DTO documentation, hateoas is not relevant here
    @Override
    public Map<String, Link> getLinks() {
        return links;
    }

    @NonNull
    @JsonAnyGetter
    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    public String getLabel(String key) {
        return labels.get(key);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Any-setter default implementation for deserialization.
     *
     * @param key   label key
     * @param value label value (string|string[]|number|list|map)
     */
    @JsonAnySetter
    void setLabel(@NonNull final String key, final Object value) {
        if (!StringUtils.hasLength(key)) {
            throw new IllegalArgumentException("Label key is empty.");
        }

        if (value instanceof String) {
            getLabels().put(key.toLowerCase(), (String) value);
            return;
        }

        if (value instanceof Number) {
            getLabels().put(key.toLowerCase(), String.valueOf(value));
            return;
        }

        if (value instanceof String[]) {
            Arrays.stream(((String[]) value)).forEach(s -> setPrefixed(key, s));
            return;
        }

        if (value instanceof List) {
            try {
                //noinspection unchecked,rawtypes
                ((List) value).forEach(s -> setPrefixed(key, (String) s));
                return;
            } catch (ClassCastException e) {
                throw new ProcessingException(String.format("Cannot set '%s' to list '%s'. Is this a list-like structure", key, value), e);
            }
        }

        if (value instanceof Map) {
            throw new IllegalArgumentException(String.format("Cannot use the value of '%s' as map ('%s'). Please check the spelling of", key, value));
        }

        getLabels().put(key, String.valueOf(value));
    }

    public Set<RelationDescription> getRelations() {
        return relations;
    }

    /**
     * Setter for relation targets (via labels).
     *
     * @param relations target identifiers
     */
    @JsonIgnore
    public void setRelations(List<String> relations) {
        if (!StringUtils.hasLength(identifier)) {
            throw new IllegalStateException("Item has no identifier");
        }
        relations.stream()
                .filter(StringUtils::hasLength)
                .map(s -> RelationFactory.createDataflowDescription(this, s))
                .forEach(this::addOrReplaceRelation);
    }

    @JsonIgnore
    public void setRelations(Set<String> relations) {
        setRelations(new ArrayList<>(relations));
    }

    /**
     * Add or update a relation description.
     *
     * If an equal relation description exist, it is updated with values from the newer one.
     *
     * @param description relation dto to be added
     */
    public void addOrReplaceRelation(@NonNull final RelationDescription description) {
        RelationDescription relationDescription = Objects.requireNonNull(description).findMatching(this.relations)
                .map(existingRelation -> {
                    existingRelation.update(description);
                    return existingRelation;
                })
                .orElse(description);
        this.relations.add(relationDescription);
    }

    /**
     * @return the fully qualified identifier for this component
     */
    @Override
    public String toString() {
        return getFullyQualifiedIdentifier().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (o.getClass() != this.getClass()) {
            return false;
        }
        return toString().equals(o.toString());
    }
}
