package de.bonndan.nivio.input.kubernetes.itemadapters;

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

public class StatefulSetItemAdapter implements ItemAdapter {
    private final StatefulSet statefulSet;

    public StatefulSetItemAdapter(StatefulSet statefulSet) {
        this.statefulSet = statefulSet;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return statefulSet;
    }

    @Override
    public String getName() {
        return statefulSet.getMetadata().getName();
    }

    @Override
    public String getUid() {
        return statefulSet.getMetadata().getUid();
    }

    @Override
    public String getNamespace() {
        return statefulSet.getMetadata().getNamespace();
    }

    @Override
    public String getCreationTimestamp() {
        return statefulSet.getMetadata().getCreationTimestamp();
    }

    public Integer getReadyReplicas() {
        return statefulSet.getStatus().getReadyReplicas();
    }

    public Integer getReplicas() {
        return statefulSet.getSpec().getReplicas();
    }

    public static List<K8sItem> getStatefulSetItems(KubernetesClient client) {
        var statefulSetList = client.apps().statefulSets().list().getItems();
        return statefulSetList.stream().map(statefulSet -> new K8sItemBuilder(ItemType.STATEFULSET, new StatefulSetItemAdapter(statefulSet)).addStatus(new ReplicaStatus()).build()).collect(Collectors.toList());
    }
}
