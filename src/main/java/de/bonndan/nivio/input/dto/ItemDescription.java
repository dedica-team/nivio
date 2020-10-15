package de.bonndan.nivio.input.dto;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * This is representation of a service in the textual form as described in a source file.
 */
public class ItemDescription implements Labeled, Linked, Tagged {

    private final Map<String, Link> links = new HashMap<>();
    @JsonDeserialize(contentAs = RelationDescription.class)
    private final Set<RelationDescription> relations = new HashSet<>();
    private final Map<String, String> labels = new HashMap<>();
    @NotEmpty
    private String environment;
    @NotEmpty
    private String identifier;
    @NotEmpty
    private String name;
    private String owner;
    private String description;
    private String contact;
    private String group;
    @JsonDeserialize(contentAs = InterfaceDescription.class)
    private Set<InterfaceDescription> interfaces = new HashSet<>();
    private List<String> providedBy = new ArrayList<>();
    private String icon;
    private String color;

    public ItemDescription() {
    }

    public ItemDescription(String identifier) {
        this.identifier = identifier;
    }

    public ItemDescription(FullyQualifiedIdentifier fqi) {
        this.identifier = fqi.getItem();
        this.group = fqi.getGroup();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = StringUtils.trimAllWhitespace(identifier);
    }

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
        return getLabel(Label.type);
    }

    public void setType(String type) {
        this.setLabel(Label.type, type);
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

    public void setLifecycle(String lifecycle) {

        //try to standardize using enum values
        if (!StringUtils.isEmpty(lifecycle)) {
            Lifecycle from = Lifecycle.from(lifecycle);
            if (from != null) {
                lifecycle = from.name();
            }
        }

        if (lifecycle != null) {
            this.setLabel(Label.lifecycle, lifecycle);
        }
    }

    @Override
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
                .filter(s -> !StringUtils.isEmpty(s))
                .map(s -> RelationBuilder.createDataflowDescription(this, s))
                .forEach(this::addRelation);
    }

    public void addRelation(RelationDescription relationItem) {
        Objects.requireNonNull(relationItem);
        this.relations.add(relationItem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
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
        if (StringUtils.isEmpty(environment)) {
            return identifier;
        }

        return FullyQualifiedIdentifier.build(environment, group, identifier).toString();
    }

    /**
     * Legacy setter for {@link StatusValue}.
     *
     * @param statuses a list of key-value pairs, keys are "label", "status", "message"
     */
    public void setStatuses(List<LinkedHashMap<String, String>> statuses) {
        statuses.forEach(map -> {
            String key = map.get("label");
            if (key != null) {
                String value = map.get(StatusValue.LABEL_SUFFIX_STATUS);
                String message = map.get(StatusValue.LABEL_SUFFIX_MESSAGE);
                setLabel(Label.key(Label.status, key, StatusValue.LABEL_SUFFIX_STATUS), value);
                setLabel(Label.key(Label.status, key, StatusValue.LABEL_SUFFIX_MESSAGE), message);
            }
        });
    }

    @Override
    @JsonAnyGetter
    public String getLabel(String key) {
        return labels.get(key);
    }

    @Override
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    @JsonAnySetter
    public void setLabel(String key, Object value) {
        if (value instanceof String) {
            labels.put(key.toLowerCase(), (String) value);
            return;
        }
        if (value instanceof String[]) {
            Arrays.stream(((String[]) value)).forEach(s -> setPrefixed(key, s));
            return;
        }

        if (value instanceof List) {
            try {
                ((List) value).forEach(s -> setPrefixed(key, (String) s));
                return;
            } catch (ClassCastException e) {
                throw new ProcessingException("Cannot set " + key + " to " + value, e);
            }
        }

        if (value instanceof Map) {
            throw new IllegalArgumentException("Cannot set " + key + " to map " + value);
        }

        labels.put(key, String.valueOf(value));
    }
}
