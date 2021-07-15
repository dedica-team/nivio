package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public Map<String, String> getStatus(Map<String, String> status) {
        return null;
    }

    @Override
    public Map<String, String> getDetails() {
        var details = new HashMap<String, String>();
        details.put("phase status", persistentVolumeClaim.getStatus().getPhase());
        details.put("storage class", persistentVolumeClaim.getSpec().getStorageClassName());
        return details;
    }

    public static List<K8sItem> getPersistentVolumeClaimItems(KubernetesClient client) {
        var getPersistentVolumeClaimsList = client.persistentVolumeClaims().list().getItems();
        return getPersistentVolumeClaimsList.stream().map(persistentVolumeClaims -> new K8sItem(persistentVolumeClaims.getMetadata().getName(), persistentVolumeClaims.getMetadata().getUid(), ItemType.VOLUME, new PersistentVolumeClaimItem(persistentVolumeClaims))).collect(Collectors.toList());
    }
}
