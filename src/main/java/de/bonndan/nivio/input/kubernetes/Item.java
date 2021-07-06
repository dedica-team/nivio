package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.dto.RelationDescription;
import io.fabric8.kubernetes.api.model.HasMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Item {
    private final String name;

    private List<Item> owners = new ArrayList<>();

    private final List<RelationDescription> relationDescriptionList = new ArrayList<>();
    private final Map<String, String> status = new HashMap<>();
    private final String type;
    private final String uid;

    protected Item(String name, String uid, String type) {
        this.name = name;
        this.uid = uid;
        this.type = type;
    }

    public void addOwner(Item owner) {
        this.owners.add(owner);
    }

    public void addRelation(RelationDescription relationDescription) {
        relationDescriptionList.add(relationDescription);
    }

    public void addStatus(String key, String value) {
        status.put(key, value);
    }

    public String getGroup() {
        if (this.getOwner().isEmpty()) {
            return name;
        } else {
            return this.getOwner().get(0).getGroup();
        }
    }

    public String getName() {
        return name;
    }

    public List<Item> getOwner() {
        return owners;
    }

    public List<RelationDescription> getRelationDescriptionList() {
        return relationDescriptionList;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getUid() {
        return uid;
    }

    public abstract HasMetadata getWrappedItem();

    public void setOwners(List<Item> owners) {
        this.owners = owners;
    }
}
