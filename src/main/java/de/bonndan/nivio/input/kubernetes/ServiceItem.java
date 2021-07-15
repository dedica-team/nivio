package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceItem implements Item {
    private final Service service;

    public ServiceItem(Service service) {
        this.service = service;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return service;
    }

    @Override
    public Map<String, String> getStatus(Map<String, String> status) {
        return null;
    }

    @Override
    public Map<String, String> getDetails() {
        var details = new HashMap<String, String>();
        details.put("cluster IP", service.getSpec().getClusterIP());
        details.put("service type", service.getSpec().getType());
        details.put("session affinity", service.getSpec().getSessionAffinity());
        return details;
    }

    public static List<K8sItem> getServiceItems(KubernetesClient client) {
        var serviceList = client.services().list().getItems();
        return serviceList.stream().map(service -> new K8sItem(service.getMetadata().getName(), service.getMetadata().getUid(), ItemType.SERVICE, new ServiceItem(service))).collect(Collectors.toList());
    }
}
