package de.bonndan.nivio.input.dto;

import de.bonndan.nivio.model.GroupItem;

import java.net.URL;
import java.util.*;

public class GroupDescription implements GroupItem {

    private String identifier;
    private String owner;
    private String team;
    private String description;
    private String contact;
    private String color;
    private List<String> contains = new ArrayList<>();
    private Map<String, URL> links = new HashMap<>();

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getTeam() {
        return team;
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
    public Map<String, URL> getLinks() {
        return links;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setTeam(String team) {
        this.team = team;
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

    public void setLinks(Map<String, URL> links) {
        this.links = links;
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
}
