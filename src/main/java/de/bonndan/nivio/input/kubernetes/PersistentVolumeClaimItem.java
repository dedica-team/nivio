package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class PersistentVolumeClaimItem extends Item {
    private final PersistentVolumeClaim persistentVolumeClaim;

    protected PersistentVolumeClaimItem(String name, String uid, String type, PersistentVolumeClaim persistentVolumeClaim, LevelDecorator levelDecorator) {
        super(name, uid, type, levelDecorator);
        this.persistentVolumeClaim = persistentVolumeClaim;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return persistentVolumeClaim;
    }

    public static List<Item> getPersistentVolumeClaimItems(KubernetesClient client) {
        var getPersistentVolumeClaimsList = client.persistentVolumeClaims().list().getItems();
        return getPersistentVolumeClaimsList.stream().map(persistentVolumeClaims -> new PersistentVolumeClaimItem(persistentVolumeClaims.getMetadata().getName(), persistentVolumeClaims.getMetadata().getUid(), ItemType.VOLUME, persistentVolumeClaims, new LevelDecorator(1))).collect(Collectors.toList());
    }
}
