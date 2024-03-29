package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * This is representation of a service in the textual form as described in a source file.
 */
public class ItemDescription implements ComponentDescription, Labeled, Linked, Tagged, ItemComponent {

    private final Map<String, Link> links = new HashMap<>();

    @Schema(description = "Relations to other landscape items.")
    @JsonDeserialize(contentAs = RelationDescription.class)
    private final Set<RelationDescription> relations = new HashSet<>();

    @Schema(description = "Key-value pair labels for an item.")
    private final Map<String, String> labels = new HashMap<>();

    @Schema(hidden = true)
    @NotEmpty
    private String environment;

    @Schema(required = true,
            description = "Immutable unique identifier (maybe use an URN). Primary means to identify items in searches.",
            pattern = IdentifierValidation.PATTERN)
    @NotEmpty
    private String identifier;

    @Schema(description = "A human readable name/title. The name is considered when items are searched.", example = "my beautiful service")
    private String name;

    @Schema(description = "The business owner of the item. Preferably use an email address.", example = "johnson@acme.com")
    private String owner;

    @Schema(description = "A brief description.")
    private String description;

    @Schema(description = "The primary way to contact a responsible person or team. Preferably use an email address.", example = "johnson@acme.com")
    private String contact;

    @Schema(description = "The identifier of the group this item belongs in. Every item requires to be member of a group internally, so if nothing is given, the value is set to its layer.",
            example = "shipping")
    private String group;

    @Schema(description = "The technical layer", example = "infrastructure")
    private String layer;

    @Schema(description = "A collection of low level interfaces. Can be used to describe HTTP API endpoints for instance.")
    @JsonDeserialize(contentAs = InterfaceDescription.class)
    private Set<InterfaceDescription> interfaces = new HashSet<>();

    @Schema(description = "A collection of identifiers which are providers for this item (i.e. hard dependencies that are required). This is a convenience field to build relations.", example = "shipping-mysqldb")
    private List<String> providedBy = new ArrayList<>();

    @Schema(description = "An icon name or URL to set the displayed map icon. The default icon set is https://materialdesignicons.com/ and all names can be used (aliases do not work).")
    private String icon;

    @Schema(description = "Overrides the group color. Use an HTML hex color code without the leading hash.", example = "4400FF")
    private String color;

    @Schema(description = "The technical address of the item (should be an URI). Taken into account when matching relation endpoints.")
    private String address;

    @Schema(description = "The type of the item. A string describing its nature. If no icon is set, the type determines the displayed icon.", example = "service|database|volume")
    private String type;

    public ItemDescription() {
    }

    public ItemDescription(String identifier) {
        this.identifier = IdentifierValidation.getValidIdentifier(identifier);
    }

    public ItemDescription(FullyQualifiedIdentifier fqi) {
        this.identifier = fqi.getItem();
        this.group = fqi.getGroup();
    }

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = IdentifierValidation.getValidIdentifier(identifier);
    }

    @Schema(hidden = true)
    @NonNull
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(environment, group, identifier);
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    @JsonProperty("links") //this override is for DTO documentation, hateoas is not relevant here
    @Override
    public Map<String, Link> getLinks() {
        return links;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Schema(description = "The lifecycle state of an item.", allowableValues = {"PLANNED", "INTEGRATION", "TEST", "PRODUCTION", "END_OF_LIFE", "EOL"})
    public void setLifecycle(String lifecycle) {

        //try to standardize using enum values
        if (StringUtils.hasLength(lifecycle)) {
            Lifecycle from = Lifecycle.from(lifecycle);
            if (from != null) {
                lifecycle = from.name();
            }
        }

        if (lifecycle != null) {
            this.setLabel(Label.lifecycle, lifecycle);
        }
    }

    @NonNull
    @Override
    @JsonAnyGetter
    public Map<String, String> getLabels() {
        return labels;
    }

    public Set<InterfaceDescription> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<InterfaceDescription> interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * Syntactic sugar to create relations from providers.
     *
     * @return provider identifier
     */
    public List<String> getProvidedBy() {
        return providedBy;
    }

    public void setProvidedBy(List<String> providedBy) {
        this.providedBy = providedBy;
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
        relations.stream()
                .filter(StringUtils::hasLength)
                .map(s -> RelationFactory.createDataflowDescription(this, s))
                .forEach(this::addOrReplaceRelation);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemDescription)) {
            return false;
        }
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    /**
     * @return the fully qualified identifier for this service description
     */
    @Override
    public String toString() {
        if (!StringUtils.hasLength(environment)) {
            return identifier;
        }

        return FullyQualifiedIdentifier.build(environment, group, identifier).toString();
    }

    /**
     * Legacy setter for {@link StatusValue}.
     *
     * @param status a list of key-value pairs, keys are "label", "status", "message"
     */
    @Schema(name = "statuses", description = "A list of statuses that works like hardcoded KPIs.")
    public void setStatuses(List<LinkedHashMap<String, String>> status) {
        setStatus(status);
    }

    /**
     * Legacy setter for {@link StatusValue}.
     *
     * @param status a list of key-value pairs, keys are "label", "status", "message"
     */
    @Schema(name = "status", description = "A list of statuses that works like hardcoded KPIs.")
    public void setStatus(List<LinkedHashMap<String, String>> status) {
        status.forEach(map -> {
            String key = map.get("label");
            if (key != null) {
                String value = map.get(StatusValue.LABEL_SUFFIX_STATUS);
                String message = map.get(StatusValue.LABEL_SUFFIX_MESSAGE);
                setLabel(Label.withPrefix(Label.status, key, StatusValue.LABEL_SUFFIX_STATUS), value);
                setLabel(Label.withPrefix(Label.status, key, StatusValue.LABEL_SUFFIX_MESSAGE), message);
            }
        });
    }

    @Override
    public String getLabel(String key) {
        return labels.get(key);
    }

    /**
     * Setter for framework map.
     *
     * @param frameworks "name": "version"
     * @see Label
     */
    @Schema(description = "The parts used to create the item. Usually refers to technical frameworks.", type = "Map", example = "java: 8")
    public void setFrameworks(final Map<String, String> frameworks) {
        frameworks.forEach(this::setFramework);
    }

    public void setFramework(@NonNull final String key, String value) {
        setLabel(Label.framework.withPrefix(key), value);
    }

    @Override
    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }
}
