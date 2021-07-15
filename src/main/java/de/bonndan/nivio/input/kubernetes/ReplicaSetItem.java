package de.bonndan.nivio.input.kubernetes;


import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.collections.map.SingletonMap;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReplicaSetItem implements Item {
    private final ReplicaSet replicaSet;

    public ReplicaSetItem(ReplicaSet replicaSet) {
        this.replicaSet = replicaSet;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return replicaSet;
    }

    @NonNull
    public Map<String, String> getStatus(Map<String, String> status) {
        var replicaCount = replicaSet.getSpec().getReplicas();
        var replicaCountDesired = Integer.valueOf(replicaSet.getMetadata().getAnnotations().get("deployment.kubernetes.io/desired-replicas"));
        var message = String.format("condition.%s of %s Pods are ready", replicaCount, replicaCountDesired);
        //TODO Check if containers a up
        if (Objects.equals(replicaCount, replicaCountDesired)) {
            return new SingletonMap("condition.all pods are ready", Status.GREEN.toString());
        } else if (replicaCount == 0) {
            return new SingletonMap(message, Status.RED.toString());
        } else
            return new SingletonMap(message, Status.ORANGE.toString());
    }

    @Override
    public Map<String, String> getDetails() {
        return null;
    }

    public static List<K8sItem> getReplicaSetItems(KubernetesClient client) {
        var replicaSetList = client.apps().replicaSets().list().getItems();
        return replicaSetList.stream().map(replicaSet -> {
            var replicaSetItem = new K8sItem(replicaSet.getMetadata().getName(), replicaSet.getMetadata().getUid(), ItemType.REPLICASET, new ReplicaSetItem(replicaSet));
            replicaSet.getStatus().getConditions().forEach(condition -> replicaSetItem.addStatus(condition.getType(), condition.getStatus()));
            return replicaSetItem;
        }).collect(Collectors.toList());
    }
}
