package de.bonndan.nivio.input.kubernetes.items;


import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.K8sItem;
import de.bonndan.nivio.input.kubernetes.K8sItemBuilder;
import de.bonndan.nivio.input.kubernetes.status.ReplicaStatus;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class ReplicaSetItem implements Item {
    private final ReplicaSet replicaSet;

    public ReplicaSetItem(ReplicaSet replicaSet) {
        this.replicaSet = replicaSet;
    }

    @NonNull
    @Override
    public HasMetadata getWrappedItem() {
        return replicaSet;
    }


    public static List<K8sItem> getReplicaSetItems(KubernetesClient client) {
        var replicaSetList = client.apps().replicaSets().list().getItems();
        return replicaSetList.stream().map(replicaSet -> {
            var replicaSetItem = new K8sItemBuilder(replicaSet.getMetadata().getName(), replicaSet.getMetadata().getUid(), ItemType.REPLICASET, new ReplicaSetItem(replicaSet)).addStatus(new ReplicaStatus()).build();
            replicaSet.getStatus().getConditions().forEach(condition -> replicaSetItem.addStatus(condition.getType(), condition.getStatus()));
            return replicaSetItem;
        }).collect(Collectors.toList());
    }
}
