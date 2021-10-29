package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupApiModel extends ComponentApiModel {

    private final Group group;
    private final Set<ItemApiModel> items;

    public GroupApiModel(@NonNull final Group group, final Set<Item> items) {
        this.group = Objects.requireNonNull(group);
        this.hateoasLinks.putAll(group.getLinks());
        this.items = items.stream().map(item -> new ItemApiModel(item, group)).collect(Collectors.toSet());
    }

    public FullyQualifiedIdentifier getFullyQualifiedIdentifier() {
        return group.getFullyQualifiedIdentifier();
    }

    public String getName() {
        return group.getName();
    }

    public String getIdentifier() {
        return group.getIdentifier();
    }

    public String getOwner() {
        return group.getOwner();
    }

    public String getDescription() {
        return group.getDescription();
    }

    public String getContact() {
        return group.getContact();
    }

    public String getColor() {
        return group.getColor();
    }

    public Set<ItemApiModel> getItems() {
        return items;
    }

    @Override
    public Map<String, String> getLabels() {
        return getPublicLabels(group.getLabels());
    }

    public String getIcon() {
        return group.getLabel(Label._icondata);
    }

    public String getLandscapeIdentifier() {
        return group.getLandscapeIdentifier();
    }
}
