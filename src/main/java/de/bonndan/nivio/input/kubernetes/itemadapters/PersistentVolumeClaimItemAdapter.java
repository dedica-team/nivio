package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;

import java.util.List;
import java.util.Map;

public class PersistentVolumeClaimItemAdapter implements ItemAdapter {
    private final PersistentVolumeClaim persistentVolumeClaim;

    public PersistentVolumeClaimItemAdapter(PersistentVolumeClaim persistentVolumeClaim) {
        this.persistentVolumeClaim = persistentVolumeClaim;
    }

    @Override
    public Map<String, String> getLabels() {
        return persistentVolumeClaim.getMetadata().getLabels();
    }

    @Override
    public List<OwnerReference> getOwnerReferences() {
        return persistentVolumeClaim.getMetadata().getOwnerReferences();
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
