package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;

import java.util.Map;

public interface Details {
    Map<String, String> getExtendedDetails(Map<String, String> statusMap, ItemAdapter itemAdapter);
}
