package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceItem extends Item {
    private final Service service;

    protected ServiceItem(String name, String uid, String type, Service service) {
        super(name, uid, type);
        this.service = service;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return service;
    }

    public static List<ServiceItem> getServiceItems(KubernetesClient client) {
        var serviceList = client.services().list().getItems();
        return serviceList.stream().map(service -> new ServiceItem(service.getMetadata().getName(), service.getMetadata().getUid(), ItemType.SERVICE, service)).collect(Collectors.toList());
    }
}
