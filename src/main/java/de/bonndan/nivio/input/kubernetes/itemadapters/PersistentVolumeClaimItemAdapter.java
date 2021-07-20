package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import org.springframework.lang.NonNull;

public class PersistentVolumeClaimItemAdapter implements ItemAdapter {
    private final PersistentVolumeClaim persistentVolumeClaim;

    public PersistentVolumeClaimItemAdapter(PersistentVolumeClaim persistentVolumeClaim) {
        this.persistentVolumeClaim = persistentVolumeClaim;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return persistentVolumeClaim;
    }

    @Override
    public String getName() {
        return persistentVolumeClaim.getMetadata().getName();
    }

    @Override
    public String getUid() {
        return persistentVolumeClaim.getMetadata().getUid();
    }

    @Override
    public String getNamespace() {
        return persistentVolumeClaim.getMetadata().getNamespace();
    }

    @Override
    public String getCreationTimestamp() {
        return persistentVolumeClaim.getMetadata().getCreationTimestamp();
    }

    public String getPhase() {
        return persistentVolumeClaim.getStatus().getPhase();
    }

    public String getStorageClassName() {
        return persistentVolumeClaim.getSpec().getStorageClassName();
    }


}
