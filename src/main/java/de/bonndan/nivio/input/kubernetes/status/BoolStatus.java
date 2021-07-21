package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class BoolStatus implements Status {
    @Override
    public Map<String, String> getExtendedStatus(@NonNull Map<String, String> statusMap, @Nullable ItemAdapter itemAdapter) {
        return statusMap.entrySet().stream().collect(Collectors.toMap(
                pair -> "k8s.boolcondition." + pair.getKey().toLowerCase(),
                pair -> pair.getValue().toLowerCase(Locale.ROOT)
        ));
    }
}
