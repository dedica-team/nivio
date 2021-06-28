package de.bonndan.nivio.input.kubernetes;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;

public class PodItem extends Item {
    Pod pod;

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return pod;
    }
}
