package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.HasMetadata;

public interface ItemAdapter {

    HasMetadata getWrappedItem();

    String getUid();

    String getName();

    String getNamespace();

    String getCreationTimestamp();
}
