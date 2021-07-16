package de.bonndan.nivio.input.kubernetes.items;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.K8sItem;
import de.bonndan.nivio.input.kubernetes.K8sItemBuilder;
import de.bonndan.nivio.input.kubernetes.status.BoolStatus;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class PodItem implements Item {
    private final Pod pod;

    public PodItem(Pod pod) {
        this.pod = pod;
    }

    @NonNull
    public HasMetadata getWrappedItem() {
        return pod;
    }


    public static List<K8sItem> getPodItems(KubernetesClient client) {
        var pods = client.pods().list().getItems();
        return pods.stream().map(pod -> {
            var podItem = new K8sItemBuilder(pod.getMetadata().getName(), pod.getMetadata().getUid(), ItemType.POD, new PodItem(pod)).addStatus(new BoolStatus()).build();
            pod.getStatus().getConditions().forEach(condition -> podItem.addStatus(condition.getType(), condition.getStatus()));
            return podItem;
        }).collect(Collectors.toList());
    }
}
