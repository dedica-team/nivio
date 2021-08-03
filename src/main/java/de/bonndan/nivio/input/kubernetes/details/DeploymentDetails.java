package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DeploymentDetails extends DetailDecorator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentDetails.class);

    public DeploymentDetails(Details detail) {
        super(detail);
    }

    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, ItemAdapter itemAdapter) {
        var newDetailMap = new HashMap<>(detail.getExtendedDetails(statusMap, itemAdapter));
        try {
            newDetailMap.put("strategy", ((DeploymentItemAdapter) itemAdapter).getStrategyType());
        } catch (ClassCastException e) {
            LOGGER.warn(e.getMessage());
        }
        return newDetailMap;
    }

}
