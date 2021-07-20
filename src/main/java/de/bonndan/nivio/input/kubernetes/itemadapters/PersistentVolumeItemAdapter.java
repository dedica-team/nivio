package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.Quantity;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

public class PersistentVolumeItemAdapter implements ItemAdapter {
    private final PersistentVolume persistentVolume;

    public PersistentVolumeItemAdapter(PersistentVolume persistentVolume) {
        this.persistentVolume = persistentVolume;
    }


    @NonNull
    public HasMetadata getWrappedItem() {
        return persistentVolume;
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


}
