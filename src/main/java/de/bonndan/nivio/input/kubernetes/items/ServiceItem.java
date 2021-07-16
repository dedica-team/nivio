package de.bonndan.nivio.input.kubernetes.items;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.K8sItem;
import de.bonndan.nivio.input.kubernetes.K8sItemBuilder;
import de.bonndan.nivio.input.kubernetes.details.DefaultDetails;
import de.bonndan.nivio.input.kubernetes.details.ServiceDetails;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
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


    public static List<K8sItem> getServiceItems(KubernetesClient client) {
        var serviceList = client.services().list().getItems();
        return serviceList.stream().map(service -> new K8sItemBuilder(service.getMetadata().getName(), service.getMetadata().getUid(), ItemType.SERVICE, new ServiceItem(service)).addDetails(new ServiceDetails(new DefaultDetails())).build()).collect(Collectors.toList());
    }
}
