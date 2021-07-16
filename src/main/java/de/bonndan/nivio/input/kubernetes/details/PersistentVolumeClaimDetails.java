package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.items.Item;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;

import java.util.HashMap;
import java.util.Map;

public class PersistentVolumeClaimDetails extends DetailDecorator {
    public PersistentVolumeClaimDetails(Details detail) {
        super(detail);
    }

    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, Item item) {
        var mewDetailMap = new HashMap<>(detail.getExtendedDetails(statusMap, item));
        var persistentVolumeClaim = (PersistentVolumeClaim) item.getWrappedItem();
        mewDetailMap.put("phase status", persistentVolumeClaim.getStatus().getPhase());
        mewDetailMap.put("storage class", persistentVolumeClaim.getSpec().getStorageClassName());
        return mewDetailMap;
    }
}
