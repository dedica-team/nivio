package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.Quantity;

import java.util.List;
import java.util.Map;

public class PersistentVolumeItemAdapter implements ItemAdapter {
    private final PersistentVolume persistentVolume;

    public PersistentVolumeItemAdapter(PersistentVolume persistentVolume) {
        this.persistentVolume = persistentVolume;
    }

    @Override
    public Map<String, String> getLabels() {
        return persistentVolume.getMetadata().getLabels();
    }

    @Override
    public List<OwnerReference> getOwnerReferences() {
        return persistentVolume.getMetadata().getOwnerReferences();
    }

    @Override
    public String getName() {
        return persistentVolume.getMetadata().getName();
    }

    @Override
    public String getUid() {
        return persistentVolume.getMetadata().getUid();
    }

    @Override
    public String getNamespace() {
        return persistentVolume.getMetadata().getNamespace();
    }

    @Override
    public String getCreationTimestamp() {
        return persistentVolume.getMetadata().getCreationTimestamp();
    }

    public String getPhase() {
        return persistentVolume.getStatus().getPhase();
    }

    public String getStorageClassName() {
        return persistentVolume.getSpec().getStorageClassName();
    }

    public String getPersistentVolumeReclaimPolicy() {
        return persistentVolume.getSpec().getPersistentVolumeReclaimPolicy();
    }

    public List<String> getAccessModes() {
        return persistentVolume.getSpec().getAccessModes();
    }

    public Map<String, Quantity> getCapacity() {
        return persistentVolume.getSpec().getCapacity();
    }

    public ObjectReference getClaimRef() {
        return persistentVolume.getSpec().getClaimRef();
    }

}
