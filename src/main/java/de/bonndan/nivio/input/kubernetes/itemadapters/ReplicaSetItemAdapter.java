package de.bonndan.nivio.input.kubernetes.itemadapters;


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

public class ReplicaSetItemAdapter implements ItemAdapter {
    private final ReplicaSet replicaSet;

    public ReplicaSetItemAdapter(ReplicaSet replicaSet) {
        this.replicaSet = replicaSet;
    }

    @NonNull
    @Override
    public HasMetadata getWrappedItem() {
        return replicaSet;
    }

    @Override
    public String getName() {
        return replicaSet.getMetadata().getName();
    }

    @Override
    public String getUid() {
        return replicaSet.getMetadata().getUid();
    }

    @Override
    public String getNamespace() {
        return replicaSet.getMetadata().getNamespace();
    }

    @Override
    public String getCreationTimestamp() {
        return replicaSet.getMetadata().getCreationTimestamp();
    }

    public Integer getReadyReplicas() {
        return replicaSet.getStatus().getReadyReplicas();
    }

    public Integer getReplicas() {
        return replicaSet.getSpec().getReplicas();
    }


    public static List<K8sItem> getReplicaSetItems(KubernetesClient client) {
        var replicaSetList = client.apps().replicaSets().list().getItems();
        return replicaSetList.stream().map(replicaSet -> {
            var replicaSetItem = new K8sItemBuilder(ItemType.REPLICASET, new ReplicaSetItemAdapter(replicaSet)).addStatus(new ReplicaStatus()).build();
            replicaSet.getStatus().getConditions().forEach(condition -> replicaSetItem.addStatus(condition.getType(), condition.getStatus()));
            return replicaSetItem;
        }).collect(Collectors.toList());
    }
}
