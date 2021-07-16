package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.items.Item;
import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.HashMap;
import java.util.Map;

public class DeploymentDetails extends DetailDecorator {
    public DeploymentDetails(Details detail) {
        super(detail);
    }

    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, Item item) {
        var newDetailMap = new HashMap<>(detail.getExtendedDetails(statusMap, item));
        newDetailMap.put("strategy", ((Deployment) item.getWrappedItem()).getSpec().getStrategy().getType());
        return newDetailMap;
    }
}
