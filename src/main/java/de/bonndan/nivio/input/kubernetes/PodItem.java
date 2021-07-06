package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PodItem extends Item {
    private final Pod pod;

    protected PodItem(String name, String uid, String type, Pod pod) {
        super(name, uid, type);
        this.pod = pod;
    }

    @Override
    public HasMetadata getWrappedItem() {
        return pod;
    }

    @Override
    public Map<String, String> getStatus() {
        return super.getStatus().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                pair -> {
                    if (pair.getValue().toLowerCase(Locale.ROOT).equals("true")) {
                        return Status.GREEN.toString();
                    } else {
                        return Status.RED.toString();
                    }
                }));
    }

    public static List<PodItem> getPodItems(KubernetesClient client) {
        var pods = client.pods().list().getItems();
        return pods.stream().map(pod -> {
            var podItem = new PodItem(pod.getMetadata().getName(), pod.getMetadata().getUid(), ItemType.POD, pod);
            pod.getStatus().getConditions().forEach(condition -> podItem.addStatus(condition.getType(), condition.getStatus()));
            return podItem;
        }).collect(Collectors.toList());
    }
}
