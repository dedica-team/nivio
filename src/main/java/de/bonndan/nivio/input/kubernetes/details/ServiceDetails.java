package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.ServiceItemAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ServiceDetails extends DetailDecorator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDetails.class);


    public ServiceDetails(Details detail) {
        super(detail);
    }

    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, ItemAdapter itemAdapter) {
        var mewDetailMap = new HashMap<>(detail.getExtendedDetails(statusMap, itemAdapter));

        try {
            var service = (ServiceItemAdapter) itemAdapter;
            mewDetailMap.put("cluster ip", service.getClusterIP());
            mewDetailMap.put("service type", service.getType());
            mewDetailMap.put("session affinity", service.getSessionAffinity());
        } catch (ClassCastException e) {
            LOGGER.warn(e.getMessage());
        }
        return mewDetailMap;
    }
}
