package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.items.Item;

import java.util.HashMap;
import java.util.Map;

public class DefaultStatus implements Status {
    @Override
    public Map<String, String> getExtendedStatus(Map<String, String> statusMap, Item item) {
        return new HashMap<>();
    }
}
