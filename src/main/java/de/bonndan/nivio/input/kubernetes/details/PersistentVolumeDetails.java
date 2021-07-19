package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeItemAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PersistentVolumeDetails extends DetailDecorator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentVolumeDetails.class);

    public PersistentVolumeDetails(Details detail) {
        super(detail);
    }

    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, ItemAdapter itemAdapter) {
        var newDetailMap = new HashMap<>(detail.getExtendedDetails(statusMap, itemAdapter));
        try {
            var persistentVolume = (PersistentVolumeItemAdapter) itemAdapter;
            newDetailMap.put("phase status", persistentVolume.getPhase());
            newDetailMap.put("storage class", persistentVolume.getStorageClassName());
            newDetailMap.put("reclaim policy", persistentVolume.getPersistentVolumeReclaimPolicy());
            var accessMode = new StringBuilder();
            for (String entity : persistentVolume.getAccessModes()) {
                accessMode.append(entity).append("\n");
            }
            newDetailMap.put("storage mode", accessMode.toString());
            persistentVolume.getCapacity().forEach((key, value) -> newDetailMap.put(key, value.getAmount() + value.getFormat()));
        } catch (ClassCastException e) {
            LOGGER.warn(e.getMessage());
        }

        return newDetailMap;
    }
}
