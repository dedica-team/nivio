package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.details.*;
import de.bonndan.nivio.input.kubernetes.itemadapters.*;
import de.bonndan.nivio.input.kubernetes.status.BoolStatus;
import de.bonndan.nivio.input.kubernetes.status.ReplicaStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class CreateItems {
    private CreateItems() {
    }

    public static List<K8sItem> getDeploymentItems(@NonNull KubernetesClient client) {
        var deploymentList = client.apps().deployments().list().getItems();
        return deploymentList.stream().map(deployment -> {
            var deploymentItem = new K8sItemBuilder(ItemType.DEPLOYMENT, new DeploymentItemAdapter(deployment)).addStatus(new BoolStatus()).addDetails(new DeploymentDetails(new DefaultDetails())).build();
            deployment.getStatus().getConditions().forEach(condition -> deploymentItem.addStatus(condition.getType(), condition.getStatus()));
            return deploymentItem;
        }).collect(Collectors.toList());
    }

    public static List<K8sItem> getPersistentVolumeClaimItems(KubernetesClient client) {
        var getPersistentVolumeClaimsList = client.persistentVolumeClaims().list().getItems();
        return getPersistentVolumeClaimsList.stream().map(persistentVolumeClaims -> new K8sItemBuilder(ItemType.VOLUME, new PersistentVolumeClaimItemAdapter(persistentVolumeClaims)).addDetails(new PersistentVolumeClaimDetails(new DefaultDetails())).build()).collect(Collectors.toList());
    }

    public static List<K8sItem> getPersistentVolumeItems(KubernetesClient client) {
        var getPersistentVolumeList = client.persistentVolumes().list().getItems();
        return getPersistentVolumeList.stream().map(persistentVolume -> new K8sItemBuilder(ItemType.VOLUME, new PersistentVolumeItemAdapter(persistentVolume)).addDetails(new PersistentVolumeDetails(new DefaultDetails())).build()).collect(Collectors.toList());
    }

    public static List<K8sItem> getPodItems(KubernetesClient client) {
        var pods = client.pods().list().getItems();
        return pods.stream().map(pod -> {
            var podItem = new K8sItemBuilder(ItemType.POD, new PodItemAdapter(pod)).addStatus(new BoolStatus()).build();
            pod.getStatus().getConditions().forEach(condition -> podItem.addStatus(condition.getType(), condition.getStatus()));
            return podItem;
        }).collect(Collectors.toList());
    }

    public static List<K8sItem> getReplicaSetItems(KubernetesClient client) {
        var replicaSetList = client.apps().replicaSets().list().getItems();
        return replicaSetList.stream().map(replicaSet -> {
            var replicaSetItem = new K8sItemBuilder(ItemType.REPLICASET, new ReplicaSetItemAdapter(replicaSet)).addStatus(new ReplicaStatus()).build();
            replicaSet.getStatus().getConditions().forEach(condition -> replicaSetItem.addStatus(condition.getType(), condition.getStatus()));
            return replicaSetItem;
        }).collect(Collectors.toList());
    }

    public static List<K8sItem> getServiceItems(KubernetesClient client) {
        var serviceList = client.services().list().getItems();
        return serviceList.stream().map(service -> new K8sItemBuilder(ItemType.SERVICE, new ServiceItemAdapter(service)).addDetails(new ServiceDetails(new DefaultDetails())).build()).collect(Collectors.toList());
    }

    public static List<K8sItem> getStatefulSetItems(KubernetesClient client) {
        var statefulSetList = client.apps().statefulSets().list().getItems();
        return statefulSetList.stream().map(statefulSet -> new K8sItemBuilder(ItemType.STATEFULSET, new StatefulSetItemAdapter(statefulSet)).addStatus(new ReplicaStatus()).build()).collect(Collectors.toList());
    }
}
