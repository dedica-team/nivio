package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.kubernetes.details.Details;
import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import de.bonndan.nivio.input.kubernetes.status.Status;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * The K8sItem Class is intended to unify the K8s Objects up to an certain point and to store relation data between those.
 */


public class K8sItem {
    private final List<RelationDescription> relationDescriptionList = new ArrayList<>();
    private final Map<String, String> labelMap = new HashMap<>();
    private final List<K8sItem> owners = new ArrayList<>();

    private final LevelDecorator levelDecorator;
    private final ItemAdapter itemAdapter;
    private final Details details;
    private final Status status;
    private final String type;

    public K8sItem(String type, ItemAdapter itemAdapter, Status status, Details details) {
        this.levelDecorator = new LevelDecorator(K8sJsonParser.getExperimentalLevel(itemAdapter.getClass()));
        this.itemAdapter = itemAdapter;
        this.details = details;
        this.status = status;
        this.type = type;
    }

    public void addOwner(@NonNull K8sItem owner) {
        this.owners.add(Objects.requireNonNull(owner));
    }

    public void addRelation(@NonNull RelationDescription relationDescription) {
        relationDescriptionList.add(Objects.requireNonNull(relationDescription));
    }

    public void addStatus(@NonNull String key, @NonNull String value) {
        labelMap.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
    }

    @NonNull
    public String getGroup() {
        if (this.getOwner().isEmpty()) {
            return itemAdapter.getName();
        } else {
            return this.getOwner().get(0).getGroup();
        }
    }

    public LevelDecorator getLevelDecorator() {
        return levelDecorator;
    }

    @NonNull
    public String getName() {
        return itemAdapter.getName();
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
    public Map<String, String> getDetails() {
        var newLabelMap = new HashMap<String, String>();
        if (status != null) {
            newLabelMap.putAll(status.getExtendedStatus(labelMap, itemAdapter));
        }
        var test = details.getExtendedDetails(labelMap, itemAdapter);
        newLabelMap.putAll(test);
        return newLabelMap;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public String getUid() {
        return itemAdapter.getUid();
    }

    @NonNull
    public ItemAdapter getItemAdapter() {
        return itemAdapter;
    }

}

