package de.bonndan.nivio.input.kubernetes;


import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.collections.map.SingletonMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReplicaSetItem extends Item {
    private final ReplicaSet replicaSet;

    protected ReplicaSetItem(String name, String uid, String type, ReplicaSet replicaSet) {
        super(name, uid, type);
        this.replicaSet = replicaSet;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return replicaSet;
    }

    @Override
    public Map<String, String> getStatus() {
        var replicaCount = replicaSet.getSpec().getReplicas();
        var replicaCountDesired = Integer.valueOf(replicaSet.getMetadata().getAnnotations().get("deployment.kubernetes.io/desired-replicas"));
        var message = String.format("%s of %s Pods are ready", replicaCount, replicaCountDesired);
        if (Objects.equals(replicaCount, replicaCountDesired)) {
            return new SingletonMap(message, Status.GREEN.toString());
        } else if (replicaCount == 0) {
            return new SingletonMap(message, Status.RED.toString());
        } else
            return new SingletonMap(message, Status.ORANGE.toString());
    }

    public static List<ReplicaSetItem> getReplicaSetItems(KubernetesClient client) {
        var replicaSetList = client.apps().replicaSets().list().getItems();
        return replicaSetList.stream().map(replicaSet -> {
            var replicaSetItem = new ReplicaSetItem(replicaSet.getMetadata().getName(), replicaSet.getMetadata().getUid(), ItemType.REPLICASET, replicaSet);
            replicaSet.getStatus().getConditions().forEach(condition -> replicaSetItem.addStatus(condition.getType(), condition.getStatus()));
            return replicaSetItem;
        }).collect(Collectors.toList());
    }
}
