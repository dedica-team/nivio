package de.bonndan.nivio.input.kubernetes.itemadapters;


import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import org.springframework.lang.NonNull;

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


}
