package de.bonndan.nivio.input.kubernetes;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;

public class ServiceItem extends Item {
    Service service;

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return service;
    }
}
