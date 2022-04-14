package de.bonndan.nivio.model;

import de.bonndan.nivio.output.Color;

import java.util.Objects;

public final class GroupBuilder extends GraphNodeBuilder<GroupBuilder, Group, Context> {

    private String color;
    private String icon;

    private GroupBuilder() {
    }

    @Override
    public GroupBuilder getThis() {
        return this;
    }

    @Deprecated
    public static GroupBuilder aTestGroup(String identifier) {
        return new GroupBuilder().withIdentifier(identifier).withParent(ContextBuilder.aTestContext("default").build());
    }

    public static GroupBuilder aGroup() {
        return new GroupBuilder();
    }

    public GroupBuilder withColor(String color) {
        this.color = color;
        return getThis();
    }

    public GroupBuilder withIcon(String icon) {
        this.icon = icon;
        return getThis();
    }

    @Override
    public Group build() {

        Group group = new Group(identifier, name, owner, contact, description, type, Objects.requireNonNull(parent, "Group has no parent context"));
        group.setLinks(links);
        group.setLabels(labels);

        if (color == null) {
            color = Color.nameToRGB(identifier, Color.DARKGRAY);
        }
        group.setLabel(Label.color.name(), Color.safe(color));

        if (icon != null) {
            group.setLabel(Label.icon.name(), icon);
        }
        return group;
    }
}
