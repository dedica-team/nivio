package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.items.Item;

import java.util.HashMap;
import java.util.Map;

public class DefaultDetails implements Details {
    @Override
    public Map<String, String> getExtendedDetails(Map<String, String> statusMap, Item item) {
        var labels = new HashMap<String, String>();
        labels.putIfAbsent("name", item.getWrappedItem().getMetadata().getName());
        labels.putIfAbsent("namespace", item.getWrappedItem().getMetadata().getNamespace());
        labels.putIfAbsent("creation", item.getWrappedItem().getMetadata().getCreationTimestamp());
        return labels;
    }
}
