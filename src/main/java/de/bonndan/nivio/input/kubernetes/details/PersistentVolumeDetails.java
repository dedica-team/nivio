package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.items.Item;
import io.fabric8.kubernetes.api.model.PersistentVolume;

import java.util.HashMap;
import java.util.Map;

public class PersistentVolumeDetails extends DetailDecorator {
    public PersistentVolumeDetails(Details detail) {
        super(detail);
    }

    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, Item item) {
        var newDetailMap = new HashMap<>(detail.getExtendedDetails(statusMap, item));
        var persistentVolume = (PersistentVolume) item.getWrappedItem();
        newDetailMap.put("phase status", persistentVolume.getStatus().getPhase());
        newDetailMap.put("storage class", persistentVolume.getSpec().getStorageClassName());
        newDetailMap.put("reclaim policy", persistentVolume.getSpec().getPersistentVolumeReclaimPolicy());
        var accessMode = new StringBuilder();
        for (String entity : persistentVolume.getSpec().getAccessModes()) {
            accessMode.append(entity).append("\n");
        }
        newDetailMap.put("storage mode", accessMode.toString());
        persistentVolume.getSpec().getCapacity().forEach((key, value) -> newDetailMap.put(key, value.getAmount() + value.getFormat()));
        return newDetailMap;
    }
}
