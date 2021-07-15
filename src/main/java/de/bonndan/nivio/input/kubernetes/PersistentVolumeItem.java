package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public Map<String, String> getStatus(Map<String, String> status) {
        return null;
    }

    @Override
    public Map<String, String> getDetails() {
        var details = new HashMap<String, String>();
        details.put("phase status", persistentVolume.getStatus().getPhase());
        details.put("storage class", persistentVolume.getSpec().getStorageClassName());
        details.put("reclaim policy", persistentVolume.getSpec().getPersistentVolumeReclaimPolicy());
        var accessMode = new StringBuilder();
        for (String entity : persistentVolume.getSpec().getAccessModes()) {
            accessMode.append(entity).append("\n");
        }
        details.put("storage mode", accessMode.toString());
        persistentVolume.getSpec().getCapacity().forEach((key, value) -> details.put(key, value.getAmount() + value.getFormat()));
        return details;
    }

    public static List<K8sItem> getPersistentVolumeItems(KubernetesClient client) {
        var getPersistentVolumeList = client.persistentVolumes().list().getItems();
        return getPersistentVolumeList.stream().map(persistentVolume -> new K8sItem(persistentVolume.getMetadata().getName(), persistentVolume.getMetadata().getUid(), ItemType.VOLUME, new PersistentVolumeItem(persistentVolume))).collect(Collectors.toList());
    }
}
