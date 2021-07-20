package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import org.springframework.lang.NonNull;

public class PodItemAdapter implements ItemAdapter {
    private final Pod pod;

    public PodItemAdapter(Pod pod) {
        this.pod = pod;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return pod;
    }

    @Override
    public String getName() {
        return pod.getMetadata().getName();
    }

    @Override
    public String getUid() {
        return pod.getMetadata().getUid();
    }

    @Override
    public String getNamespace() {
        return pod.getMetadata().getNamespace();
    }

    @Override
    public String getCreationTimestamp() {
        return pod.getMetadata().getCreationTimestamp();
    }


}
