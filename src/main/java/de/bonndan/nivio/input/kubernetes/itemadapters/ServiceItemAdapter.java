package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import org.springframework.lang.NonNull;

public class ServiceItemAdapter implements ItemAdapter {
    private final Service service;

    public ServiceItemAdapter(Service service) {
        this.service = service;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return service;
    }

    @Override
    public String getName() {
        return service.getMetadata().getName();
    }

    @Override
    public String getUid() {
        return service.getMetadata().getUid();
    }

    @Override
    public String getNamespace() {
        return service.getMetadata().getNamespace();
    }

    @Override
    public String getCreationTimestamp() {
        return service.getMetadata().getCreationTimestamp();
    }

    public String getClusterIP() {
        return service.getSpec().getClusterIP();
    }

    public String getType() {
        return service.getSpec().getType();
    }

    public String getSessionAffinity() {
        return service.getSpec().getSessionAffinity();
    }


}
