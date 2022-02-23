package de.bonndan.nivio.search;

import de.bonndan.nivio.model.ComponentClass;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Link;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

public class SearchDocumentValueObject {

    private final URI fullyQualifiedIdentifier;
    private final String identifier;
    private final String parentIdentifier;
    private final ComponentClass component;
    private final String name;
    private final String description;
    private final String owner;
    private final String[] tags;
    private final String layer;
    private final String type;
    private final Map<String, Link> links;
    private Map<String, String> labels;
    private final String group;
    private final String address;

    public SearchDocumentValueObject(URI fullyQualifiedIdentifier,
                                     String identifier,
                                     String parentIdentifier,
                                     ComponentClass component,
                                     String name,
                                     String description,
                                     String owner,
                                     String[] tags,
                                     String type,
                                     Map<String, Link> links,
                                     Map<String, String> labels,
                                     String layer,
                                     String group,
                                     String address
    ) {
        this.fullyQualifiedIdentifier = fullyQualifiedIdentifier;
        this.identifier = identifier;
        this.parentIdentifier = parentIdentifier;
        this.component = component;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.tags = tags;
        this.layer = layer;
        this.type = type;
        this.links = links;
        this.labels = labels;
        this.group = group;
        this.address = address;
    }


    public URI getFullyQualifiedIdentifier() {
        return fullyQualifiedIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getParentIdentifier() {
        return parentIdentifier;
    }

    @NonNull
    public ComponentClass getComponentClass() {
        return component;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Optional<String> getGroup() {
        return Optional.ofNullable(group);
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public String getOwner() {
        return owner;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public String[] getTags() {
        return tags;
    }

    public Map<String, String> getLabels(Label label) {
        return Labeled.withPrefix(label.name(), getLabels());
    }

    public Optional<String> getLayer() {
        return Optional.ofNullable(layer);
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    @Override
    public String toString() {
        return "SearchDocumentValueObject{" + fullyQualifiedIdentifier + '}';
    }
}
