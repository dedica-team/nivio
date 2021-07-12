package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.dto.RelationDescription;
import org.springframework.lang.NonNull;

import java.util.*;

public class K8sItem {
    private final List<RelationDescription> relationDescriptionList = new ArrayList<>();
    private final Map<String, String> status = new HashMap<>();
    private final List<K8sItem> owners = new ArrayList<>();

    private final LevelDecorator levelDecorator;
    private final Item itemContainer;
    private final String name;
    private final String type;
    private final String uid;

    public K8sItem(String name, String uid, String type, LevelDecorator levelDecorator, Item itemContainer) {
        this.levelDecorator = levelDecorator;
        this.itemContainer = itemContainer;
        this.name = name;
        this.type = type;
        this.uid = uid;
    }

    public void addOwner(@NonNull K8sItem owner) {
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
    public List<K8sItem> getOwner() {
        return owners;
    }

    @NonNull
    public List<RelationDescription> getRelationDescriptionList() {
        return relationDescriptionList;
    }

    @NonNull
    public Map<String, String> getStatus() {
        return Objects.requireNonNullElse(itemContainer.getStatus(this.status), this.status);
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
    public Item getItemContainer() {
        return itemContainer;
    }

}

