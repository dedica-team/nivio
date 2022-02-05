package de.bonndan.nivio.model;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public final class ItemBuilder extends GraphNodeBuilder<ItemBuilder, Item, Group> {

    private Set<ServiceInterface> interfaces = new HashSet<>();
    private URI address;
    private String layer;
    private String color;
    private String icon;

    private ItemBuilder() {
    }

    @Override
    public ItemBuilder getThis() {
        return this;
    }

    public static ItemBuilder anItem() {
        return new ItemBuilder();
    }

    public ItemBuilder withInterfaces(Set<ServiceInterface> interfaces) {
        this.interfaces = interfaces;
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

    @Override
    public Item build() {
        Item item = new Item(identifier, name, owner, contact, description, color, icon, type, address, Layer.of(layer), parent);
        item.setLinks(links);
        item.getLabels().putAll(labels);
        item.setInterfaces(interfaces);
        return item;
    }
}