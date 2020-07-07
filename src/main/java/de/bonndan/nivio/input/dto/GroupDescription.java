package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.GroupItem;
import de.bonndan.nivio.model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GroupDescription implements GroupItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupDescription.class);

    private String identifier;
    private String owner;
    private String description;
    private String contact;
    private String color;
    private List<String> contains = new ArrayList<>();
    private final Map<String, Link> links = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();
    private String environment;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return FullyQualifiedIdentifier.build(environment, identifier, null);
    }

    @Override
    public String getName() {
        return identifier;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getContact() {
        return contact;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public Map<String, Link> getLinks() {
        return links;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroupDescription) {
            return identifier != null && ((GroupDescription) obj).identifier != null &&
                    identifier.equals(((GroupDescription) obj).getIdentifier());
        } else {
            return false;
        }
    }

    public List<String> getContains() {
        return contains;
    }

    public void setContains(List<String> contains) {
        this.contains = contains;
    }

    @Override
    public Map<String, String> getLabels() {
        return labels;
    }

    @Override
    public String getLabel(String key) {
        return labels.get(key);
    }

    @Override
    public void setLabel(String key, String value) {
        labels.put(key, value);
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
