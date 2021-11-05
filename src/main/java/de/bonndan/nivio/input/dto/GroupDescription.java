package de.bonndan.nivio.input.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Link;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "A group of items. Could be used as bounded context, for instance.")
public class GroupDescription implements ComponentDescription {

    @Schema(description = "Links related to the group.")
    private final Map<String, Link> links = new HashMap<>();

    @Schema(description = "Labels related to the group.")
    private final Map<String, String> labels = new HashMap<>();

    @Schema(required = true, description = "A unique identifier for the group (also used as name). Descriptions are merged based on the identifier.", example = "shipping")
    private String identifier;

    @Schema(description = "The business owner of the group.")
    private String owner;

    @Schema(description = "A brief description.")
    private String description;

    @Schema(description = "A contact method, preferably email.")
    private String contact;

    @Schema(description = "The HTML (hexcode only!) color used to draw the group and its items. If no color is given, one is computed.", example = "05ffaa")
    private String color;

    @Schema(description = "A list of item identifiers or SQL-like queries to easily assign items to this group.", example = "identifier LIKE 'DB1'")
    private List<String> contains = new ArrayList<>();

    @Schema(hidden = true)
    private String environment;

    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Schema(hidden = true, description = "Computed value")
    @NonNull
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(environment, identifier, null);
    }

    public String getName() {
        return identifier;
    }

    @Override
    public void setName(String name) {
        identifier = name;
    }

    public String getOwner() {
        return owner;
    }

    @JsonIgnore
    @Override
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroupDescription) {
            return identifier != null && ((GroupDescription) obj).identifier != null &&
                    identifier.equals(((GroupDescription) obj).getIdentifier());
        }
        return false;
    }

    public List<String> getContains() {
        return contains;
    }

    public void setContains(List<String> contains) {
        this.contains = contains;
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

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
