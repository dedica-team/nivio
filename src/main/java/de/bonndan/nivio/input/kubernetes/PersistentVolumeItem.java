package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class PersistentVolumeItem extends Item {
    private final PersistentVolume persistentVolume;

    protected PersistentVolumeItem(String name, String uid, String type, PersistentVolume persistentVolume, LevelDecorator levelDecorator) {
        super(name, uid, type, levelDecorator);
        this.persistentVolume = persistentVolume;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return persistentVolume;
    }

    public static List<Item> getPersistentVolumeItems(KubernetesClient client) {
        var getPersistentVolumeList = client.persistentVolumes().list().getItems();
        return getPersistentVolumeList.stream().map(persistentVolumeClaims -> new PersistentVolumeItem(persistentVolumeClaims.getMetadata().getName(), persistentVolumeClaims.getMetadata().getUid(), ItemType.VOLUME, persistentVolumeClaims, new LevelDecorator(0))).collect(Collectors.toList());
    }
}
