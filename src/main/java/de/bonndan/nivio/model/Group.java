package de.bonndan.nivio.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.bonndan.nivio.output.Rendered;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group implements GroupItem, Rendered {

    public static final Group DEFAULT_GROUP;
    public static final String COMMON = "Common";

    static {
        DEFAULT_GROUP = new Group();
        DEFAULT_GROUP.setIdentifier(COMMON);
    }

    private String identifier;
    private String owner;
    private String description;
    private String contact;
    private String color;
    private Map<String, URL> links = new HashMap<>();

    private Map<String, String> labels = new HashMap<>();

    /**
     * Items belonging to this group.
     */
    private List<Item> items = new ArrayList<>();

    public Group() {

    }

    public Group(String identifier) {
        setIdentifier(identifier);
    }

    @Override
    public String getIdentifier() {
        return identifier;
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
    public Map<String, URL> getLinks() {
        return links;
    }

    public void setIdentifier(String identifier) {
        if (StringUtils.isEmpty(identifier))
            identifier = COMMON;

        this.identifier = identifier;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @JsonBackReference
    public List<Item> getItems() {
        return items;
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
}
