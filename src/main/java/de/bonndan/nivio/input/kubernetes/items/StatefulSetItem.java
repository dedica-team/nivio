package de.bonndan.nivio.input.kubernetes.items;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.K8sItem;
import de.bonndan.nivio.input.kubernetes.K8sItemBuilder;
import de.bonndan.nivio.input.kubernetes.status.ReplicaStatus;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class StatefulSetItem implements Item {
    private final StatefulSet statefulSet;

    public StatefulSetItem(StatefulSet statefulSet) {
        this.statefulSet = statefulSet;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return statefulSet;
    }

    public static List<K8sItem> getStatefulSetItems(KubernetesClient client) {
        var statefulSetList = client.apps().statefulSets().list().getItems();
        return statefulSetList.stream().map(statefulSet -> new K8sItemBuilder(statefulSet.getMetadata().getName(), statefulSet.getMetadata().getUid(), ItemType.STATEFULSET, new StatefulSetItem(statefulSet)).addStatus(new ReplicaStatus()).build()).collect(Collectors.toList());
    }
}
