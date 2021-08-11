package de.bonndan.nivio.output.dto;

import de.bonndan.nivio.model.FullyQualifiedIdentifier;
import de.bonndan.nivio.model.Group;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupApiModel extends ComponentApiModel {

    private final Group group;

    public GroupApiModel(@NonNull final Group group) {
        this.group = Objects.requireNonNull(group);
        this.hateoasLinks.putAll(group.getLinks());
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
        return group.getItems().stream().map(item -> new ItemApiModel(item, group)).collect(Collectors.toSet());
    }

    @Override
    public Map<String, String> getLabels() {
        return getPublicLabels(group.getLabels());
    }

    public String getIcon() {
        return group.getIcon();
    }

    public String getLandscapeIdentifier() {
        return group.getLandscapeIdentifier();
    }
}
