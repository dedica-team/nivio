package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeClaimItemAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PersistentVolumeClaimDetails extends DetailDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentVolumeClaimDetails.class);

    public PersistentVolumeClaimDetails(Details detail) {
        super(detail);
    }

    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, ItemAdapter itemAdapter) {
        var mewDetailMap = new HashMap<>(detail.getExtendedDetails(statusMap, itemAdapter));
        try {
            var persistentVolumeClaim = (PersistentVolumeClaimItemAdapter) itemAdapter;
            mewDetailMap.put("phase status", persistentVolumeClaim.getPhase());
            mewDetailMap.put("storage class", persistentVolumeClaim.getStorageClassName());
        } catch (ClassCastException e) {
            LOGGER.warn(e.getMessage());
        }
        return mewDetailMap;
    }
}
