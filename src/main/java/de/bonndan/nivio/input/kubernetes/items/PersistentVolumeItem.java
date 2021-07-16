package de.bonndan.nivio.input.kubernetes.items;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.K8sItem;
import de.bonndan.nivio.input.kubernetes.K8sItemBuilder;
import de.bonndan.nivio.input.kubernetes.details.DefaultDetails;
import de.bonndan.nivio.input.kubernetes.details.PersistentVolumeDetails;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class PersistentVolumeItem implements Item {
    private final PersistentVolume persistentVolume;

    public PersistentVolumeItem(PersistentVolume persistentVolume) {
        this.persistentVolume = persistentVolume;
    }


    @NonNull
    public HasMetadata getWrappedItem() {
        return persistentVolume;
    }


    public static List<K8sItem> getPersistentVolumeItems(KubernetesClient client) {
        var getPersistentVolumeList = client.persistentVolumes().list().getItems();
        return getPersistentVolumeList.stream().map(persistentVolume -> new K8sItemBuilder(persistentVolume.getMetadata().getName(), persistentVolume.getMetadata().getUid(), ItemType.VOLUME, new PersistentVolumeItem(persistentVolume)).addDetails(new PersistentVolumeDetails(new DefaultDetails())).build()).collect(Collectors.toList());
    }
}
