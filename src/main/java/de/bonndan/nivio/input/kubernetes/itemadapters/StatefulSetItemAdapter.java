package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import org.springframework.lang.NonNull;

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


}
