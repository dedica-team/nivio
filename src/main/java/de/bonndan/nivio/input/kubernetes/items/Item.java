package de.bonndan.nivio.input.kubernetes.items;

import io.fabric8.kubernetes.api.model.HasMetadata;

public interface Item {
    HasMetadata getWrappedItem();
}
