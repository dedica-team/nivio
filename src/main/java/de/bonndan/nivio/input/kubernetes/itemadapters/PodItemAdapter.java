package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Volume;

import java.util.List;
import java.util.Map;

public class PodItemAdapter implements ItemAdapter {
    private final Pod pod;

    public PodItemAdapter(Pod pod) {
        this.pod = pod;
    }

    @Override
    public Map<String, String> getLabels() {
        return pod.getMetadata().getLabels();
    }

    @Override
    public List<OwnerReference> getOwnerReferences() {
        return pod.getMetadata().getOwnerReferences();
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

    public List<Volume> getVolumes() {
        return pod.getSpec().getVolumes();
    }


}
