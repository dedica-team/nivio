package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceItem extends Item {
    private final Service service;

    protected ServiceItem(String name, String uid, String type, Service service, LevelDecorator levelDecorator) {
        super(name, uid, type, levelDecorator);
        this.service = service;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return service;
    }

    public static List<Item> getServiceItems(KubernetesClient client) {
        var serviceList = client.services().list().getItems();
        return serviceList.stream().map(service -> new ServiceItem(service.getMetadata().getName(), service.getMetadata().getUid(), ItemType.SERVICE, service, new LevelDecorator(-1))).collect(Collectors.toList());
    }
}
