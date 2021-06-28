package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.dto.RelationDescription;
import io.fabric8.kubernetes.api.model.HasMetadata;

import java.util.ArrayList;
import java.util.List;

public abstract class Item {
    protected String name;
    private String uid;
    private String type;

    private final List<Item> owners = new ArrayList<>();

    private final List<RelationDescription> relationDescriptionList = new ArrayList<>();

    public List<RelationDescription> getRelationDescriptionList() {
        return relationDescriptionList;
    }

    public abstract HasMetadata getWrappedItem();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        if (this.getOwner().isEmpty()) {
            return name;
        } else {
            return this.getOwner().get(0).getGroup();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Item> getOwner() {
        return owners;
    }

    public void addOwner(Item owner) {
        this.owners.add(owner);
    }

    public void setOwners(List<Item> owners) {
        this.owners.addAll(owners);
    }

    public void addRelation(RelationDescription relationDescription) {
        relationDescriptionList.add(relationDescription);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
