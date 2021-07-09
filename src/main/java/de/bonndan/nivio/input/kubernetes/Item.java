package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.dto.RelationDescription;
import io.fabric8.kubernetes.api.model.HasMetadata;
import org.springframework.lang.NonNull;

import java.util.*;

public abstract class Item {
    private final String name;
    private List<Item> owners = new ArrayList<>();
    private final List<RelationDescription> relationDescriptionList = new ArrayList<>();
    private final Map<String, String> status = new HashMap<>();
    private final String type;
    private final String uid;

    private final LevelDecorator levelDecorator;

    protected Item(String name, String uid, String type, LevelDecorator levelDecorator) {
        this.levelDecorator = levelDecorator;
        this.name = name;
        this.uid = uid;
        this.type = type;
    }

    public void addOwner(@NonNull Item owner) {
        this.owners.add(Objects.requireNonNull(owner));
    }

    public void addRelation(@NonNull RelationDescription relationDescription) {
        relationDescriptionList.add(Objects.requireNonNull(relationDescription));
    }

    public void addStatus(@NonNull String key, @NonNull String value) {
        status.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
    }

    @NonNull
    public String getGroup() {
        if (this.getOwner().isEmpty()) {
            return name;
        } else {
            return this.getOwner().get(0).getGroup();
        }
    }

    public LevelDecorator getLevelDecorator() {
        return levelDecorator;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public List<Item> getOwner() {
        return owners;
    }

    @NonNull
    public List<RelationDescription> getRelationDescriptionList() {
        return relationDescriptionList;
    }

    @NonNull
    public Map<String, String> getStatus() {
        return status;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    @NonNull
    public abstract HasMetadata getWrappedItem();

    public void setOwners(@NonNull List<Item> owners) {
        this.owners = Objects.requireNonNull(owners);
    }
}
