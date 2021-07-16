package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.items.Item;

import java.util.Map;

public interface Status {
    Map<String, String> getExtendedStatus(Map<String, String> statusMap, Item item);
}
