package de.bonndan.nivio.input.kubernetes.itemadapters;

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

    public static List<K8sItem> getServiceItems(KubernetesClient client) {
        var serviceList = client.services().list().getItems();
        return serviceList.stream().map(service -> new K8sItemBuilder(ItemType.SERVICE, new ServiceItemAdapter(service)).addDetails(new ServiceDetails(new DefaultDetails())).build()).collect(Collectors.toList());
    }
}
