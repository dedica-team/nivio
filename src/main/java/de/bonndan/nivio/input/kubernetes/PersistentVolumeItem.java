package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.List;
import java.util.stream.Collectors;

public class PersistentVolumeItem extends Item {
    private final PersistentVolume persistentVolume;

    protected PersistentVolumeItem(String name, String uid, String type, PersistentVolume persistentVolume) {
        super(name, uid, type);
        this.persistentVolume = persistentVolume;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return persistentVolume;
    }

    public static List<PersistentVolumeItem> getPersistentVolumeItems(KubernetesClient client) {
        var getPersistentVolumeList = client.persistentVolumes().list().getItems();
        return getPersistentVolumeList.stream().map(persistentVolumeClaims -> new PersistentVolumeItem(persistentVolumeClaims.getMetadata().getName(), persistentVolumeClaims.getMetadata().getUid(), ItemType.VOLUME, persistentVolumeClaims)).collect(Collectors.toList());
    }
}
