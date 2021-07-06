package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.List;
import java.util.stream.Collectors;

public class StatefulSetItem extends Item {
    private final StatefulSet statefulSet;

    protected StatefulSetItem(String name, String uid, String type, StatefulSet statefulSet) {
        super(name, uid, type);
        this.statefulSet = statefulSet;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return statefulSet;
    }

    public static List<StatefulSetItem> getStatefulSetItems(KubernetesClient client) {
        var statefulSetList = client.apps().statefulSets().list().getItems();
        return statefulSetList.stream().map(statefulSet -> new StatefulSetItem(statefulSet.getMetadata().getName(), statefulSet.getMetadata().getUid(), ItemType.STATEFULSET, statefulSet)).collect(Collectors.toList());
    }
}
