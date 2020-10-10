package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.GroupItem;
import de.bonndan.nivio.model.Labeled;
import de.bonndan.nivio.model.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDescription implements Labeled {

    private final Map<String, Link> links = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();
    private String identifier;
    private String owner;
    private String description;
    private String contact;
    private String color;
    private List<String> contains = new ArrayList<>();
    private String environment;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(environment, identifier, null);
    }

    public String getName() {
        return identifier;
    }

    public String getOwner() {
        return owner;
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

    public Map<String, String> getLabels() {
        return labels;
    }

    public String getLabel(String key) {
        return labels.get(key);
    }

    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
