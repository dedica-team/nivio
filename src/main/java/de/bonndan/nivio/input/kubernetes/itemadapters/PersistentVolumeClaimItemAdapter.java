package de.bonndan.nivio.input.kubernetes.itemadapters;

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

    public static List<K8sItem> getPersistentVolumeClaimItems(KubernetesClient client) {
        var getPersistentVolumeClaimsList = client.persistentVolumeClaims().list().getItems();
        return getPersistentVolumeClaimsList.stream().map(persistentVolumeClaims -> new K8sItemBuilder(ItemType.VOLUME, new PersistentVolumeClaimItemAdapter(persistentVolumeClaims)).addDetails(new PersistentVolumeClaimDetails(new DefaultDetails())).build()).collect(Collectors.toList());
    }
}
