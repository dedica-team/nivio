package de.bonndan.nivio.input.kubernetes.items;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.K8sItem;
import de.bonndan.nivio.input.kubernetes.K8sItemBuilder;
import de.bonndan.nivio.input.kubernetes.details.DefaultDetails;
import de.bonndan.nivio.input.kubernetes.details.PersistentVolumeClaimDetails;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class PersistentVolumeClaimItem implements Item {
    private final PersistentVolumeClaim persistentVolumeClaim;

    public PersistentVolumeClaimItem(PersistentVolumeClaim persistentVolumeClaim) {
        this.persistentVolumeClaim = persistentVolumeClaim;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return persistentVolumeClaim;
    }

    public static List<K8sItem> getPersistentVolumeClaimItems(KubernetesClient client) {
        var getPersistentVolumeClaimsList = client.persistentVolumeClaims().list().getItems();
        return getPersistentVolumeClaimsList.stream().map(persistentVolumeClaims -> new K8sItemBuilder(persistentVolumeClaims.getMetadata().getName(), persistentVolumeClaims.getMetadata().getUid(), ItemType.VOLUME, new PersistentVolumeClaimItem(persistentVolumeClaims)).addDetails(new PersistentVolumeClaimDetails(new DefaultDetails())).build()).collect(Collectors.toList());
    }
}
