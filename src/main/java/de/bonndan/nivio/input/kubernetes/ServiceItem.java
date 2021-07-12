package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

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

    public static List<K8sItem> getServiceItems(KubernetesClient client) {
        var serviceList = client.services().list().getItems();
        return serviceList.stream().map(service -> new K8sItem(service.getMetadata().getName(), service.getMetadata().getUid(), ItemType.DEPLOYMENT, new LevelDecorator(4), new ServiceItem(service))).collect(Collectors.toList());
    }
}
