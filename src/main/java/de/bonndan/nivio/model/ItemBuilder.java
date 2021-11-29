package de.bonndan.nivio.model;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemBuilder {
    private Map<String, Link> links = new HashMap<>();
    private Set<Relation> relations = ConcurrentHashMap.newKeySet();
    private Set<ServiceInterface> interfaces = new HashSet<>();
    private String identifier;
    private Landscape landscape;
    private String name;
    private String owner;
    private String contact;
    private String description;
    private String group;
    private String color;
    private String icon;
    private String type;
    private URI address;
    private String layer;
    private Map<String, String> labels = new HashMap<>();



    private ItemBuilder() {
    }

    public static ItemBuilder anItem() {
        return new ItemBuilder();
    }

    public ItemBuilder withLinks(Map<String, Link> links) {
        this.links = links;
        return this;
    }

    public ItemBuilder withRelations(Set<Relation> relations) {
        this.relations = relations;
        return this;
    }

    public ItemBuilder withInterfaces(Set<ServiceInterface> interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    public ItemBuilder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ItemBuilder withLandscape(Landscape landscape) {
        this.landscape = landscape;
        return this;
    }

    public ItemBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public ItemBuilder withContact(String contact) {
        this.contact = contact;
        return this;
    }

    public ItemBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ItemBuilder withGroup(String group) {
        this.group = group;
        return this;
    }

    public ItemBuilder withColor(String color) {
        this.color = color;
        return this;
    }

    public ItemBuilder withIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public ItemBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public ItemBuilder withLayer(String layer) {
        this.layer = layer;
        return this;
    }

    public ItemBuilder withAddress(URI address) {
        this.address = address;
        return this;
    }

    public Item build() {
        Item item = new Item(identifier, landscape, group, name, owner, contact,
                description, color, icon, type, address, Layer.of(layer));
        item.setLinks(links);
        item.getLabels().putAll(labels);
        item.setRelations(relations);
        item.setInterfaces(interfaces);
        return item;
    }

    public ItemBuilder withLabels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }
    public String getGroup() {
        return group;
    }

}