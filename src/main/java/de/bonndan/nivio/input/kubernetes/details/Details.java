package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.items.Item;

import java.util.Map;

public interface Details {
    Map<String, String> getExtendedDetails(Map<String, String> statusMap, Item item);
}
