package de.bonndan.nivio.input.kubernetes.itemadapters;

import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.Service;

import java.util.List;
import java.util.Map;

public class ServiceItemAdapter implements ItemAdapter {
    private final Service service;

    public ServiceItemAdapter(Service service) {
        this.service = service;
    }

    @Override
    public Map<String, String> getLabels() {
        return service.getMetadata().getLabels();
    }

    @Override
    public List<OwnerReference> getOwnerReferences() {
        return service.getMetadata().getOwnerReferences();
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
