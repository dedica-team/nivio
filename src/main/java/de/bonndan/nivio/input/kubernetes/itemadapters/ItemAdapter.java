package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.OwnerReference;

import java.util.List;
import java.util.Map;

public interface ItemAdapter {

    Map<String, String> getLabels();

    String getUid();

    String getName();

    String getNamespace();

    String getCreationTimestamp();

    List<OwnerReference> getOwnerReferences();
}
