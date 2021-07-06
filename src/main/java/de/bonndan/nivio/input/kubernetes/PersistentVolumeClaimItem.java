package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.List;
import java.util.stream.Collectors;

public class PersistentVolumeClaimItem extends Item {
    private final PersistentVolumeClaim persistentVolumeClaim;

    protected PersistentVolumeClaimItem(String name, String uid, String type, PersistentVolumeClaim persistentVolumeClaim) {
        super(name, uid, type);
        this.persistentVolumeClaim = persistentVolumeClaim;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return persistentVolumeClaim;
    }

    public static List<PersistentVolumeClaimItem> getPersistentVolumeClaimItems(KubernetesClient client) {
        var getPersistentVolumeClaimsList = client.persistentVolumeClaims().list().getItems();
        return getPersistentVolumeClaimsList.stream().map(persistentVolumeClaims -> new PersistentVolumeClaimItem(persistentVolumeClaims.getMetadata().getName(), persistentVolumeClaims.getMetadata().getUid(), ItemType.VOLUME, persistentVolumeClaims)).collect(Collectors.toList());
    }
}
