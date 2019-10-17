package de.bonndan.nivio.model;

import de.bonndan.nivio.util.Color;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static de.bonndan.nivio.model.Groups.COMMON;

public class Group implements GroupItem {

    public static final Group DEFAULT_GROUP;

    static {
        DEFAULT_GROUP = new Group();
        DEFAULT_GROUP.setColor(Color.DARKGRAY);
        DEFAULT_GROUP.setIdentifier(COMMON);
    }

    private String identifier;
    private String owner;
    private String description;
    private String contact;
    private String team;
    private String color;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
