package de.bonndan.nivio.input.kubernetes;

import io.fabric8.kubernetes.api.model.HasMetadata;

import java.util.Map;

public interface Item {
    HasMetadata getWrappedItem();

    Map<String, String> getStatus(Map<String, String> status);

    Map<String, String> getDetails();
}
