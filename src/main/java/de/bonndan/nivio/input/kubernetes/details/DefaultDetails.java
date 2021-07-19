package de.bonndan.nivio.input.kubernetes.details;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultDetails implements Details {

    @NonNull
    @Override
    public Map<String, String> getExtendedDetails(@Nullable Map<String, String> statusMap, @NonNull ItemAdapter itemAdapter) {
        Objects.requireNonNull(itemAdapter);
        var labels = new HashMap<String, String>();
        labels.putIfAbsent("name", itemAdapter.getName());
        labels.putIfAbsent("namespace", itemAdapter.getNamespace());
        labels.putIfAbsent("creation", itemAdapter.getCreationTimestamp());
        return labels;
    }
}
