package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class BoolStatus implements Status {
    @Override
    public Map<String, String> getExtendedStatus(Map<String, String> statusMap, ItemAdapter itemAdapter) {
        return statusMap.entrySet().stream().collect(Collectors.toMap(
                pair -> "condition." + pair.getKey().toLowerCase(),
                pair -> {
                    if (pair.getValue().toLowerCase(Locale.ROOT).equals("true")) {
                        return de.bonndan.nivio.assessment.Status.GREEN.toString();
                    } else {
                        return de.bonndan.nivio.assessment.Status.RED.toString();
                    }
                }));
    }
}
