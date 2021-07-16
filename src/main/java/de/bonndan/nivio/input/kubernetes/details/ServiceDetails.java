package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.items.Item;
import io.fabric8.kubernetes.api.model.Service;

import java.util.HashMap;
import java.util.Map;

public class ServiceDetails extends DetailDecorator {
    public ServiceDetails(Details detail) {
        super(detail);
    }

    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, Item item) {
        var mewDetailMap = new HashMap<>(detail.getExtendedDetails(statusMap, item));
        var service = (Service) item.getWrappedItem();
        mewDetailMap.put("cluster ip", service.getSpec().getClusterIP());
        mewDetailMap.put("service type", service.getSpec().getType());
        mewDetailMap.put("session affinity", service.getSpec().getSessionAffinity());
        return mewDetailMap;
    }
}
