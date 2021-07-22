package de.bonndan.nivio.input.kubernetes.itemadapters;


import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;

import java.util.List;
import java.util.Map;

public class ReplicaSetItemAdapter implements ItemAdapter {
    private final ReplicaSet replicaSet;

    public ReplicaSetItemAdapter(ReplicaSet replicaSet) {
        this.replicaSet = replicaSet;
    }

    @Override
    public Map<String, String> getLabels() {
        return replicaSet.getMetadata().getLabels();
    }

    @Override
    public List<OwnerReference> getOwnerReferences() {
        return replicaSet.getMetadata().getOwnerReferences();
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


}
