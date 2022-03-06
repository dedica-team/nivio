package de.bonndan.nivio.output.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Link;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ComponentApiModel {

    private final URI fullyQualifiedIdentifier;
    private final String identifier;
    private final String name;
    private final String description;
    private final String owner;
    private final Map<String, String> labels;
    private final String[] tags;
    private final String type;
    private final String color;
    private final String icon;
    protected Map<String, Link> hateoasLinks = new HashMap<>();

    ComponentApiModel(@NonNull final Component component) {
        this.fullyQualifiedIdentifier = Objects.requireNonNull(component).getFullyQualifiedIdentifier();
        this.identifier = component.getIdentifier();
        this.name = component.getName();
        this.description = component.getDescription();
        this.owner = component.getOwner();
        this.type = component.getType();
        this.color = component.getColor();
        this.icon = component.getLabel(Label._icondata);

        this.hateoasLinks.putAll(component.getLinks());
        this.labels = Labeled.withoutKeys(component.getLabels(), Label.INTERNAL_LABEL_PREFIX, Label.status.name());
        this.tags = component.getTags();
    }

    @Schema(name = "_links")
    @JsonProperty("_links")
    public Map<String, Link> getLinks() {
        return hateoasLinks;
    }

    public void setHateoasLinks(Map<String, Link> hateoasLinks) {
        this.hateoasLinks.putAll(hateoasLinks);
    }

    @Schema(description = "the global identifier, unique in the landscape and showing the level")
    public URI getFullyQualifiedIdentifier() {
        return fullyQualifiedIdentifier;
    }

    @Schema(description = "Local identifier, only unique within the children of a parent")
    public String getIdentifier() {
        return identifier;
    }

    @Schema(description = "Human-readable and displayed name of the component")
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public String[] getTags() {
        return tags;
    }

    @Override
    public final String toString() {
        return fullyQualifiedIdentifier.toString();
    }
}
