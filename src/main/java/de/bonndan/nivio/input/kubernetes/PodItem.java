package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
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


    @NonNull
    public Map<String, String> getStatus(Map<String, String> status) {
        return status.entrySet().stream().collect(Collectors.toMap(
                pair -> "condition." + pair.getKey().toLowerCase(),
                pair -> {
                    if (pair.getValue().toLowerCase(Locale.ROOT).equals("true")) {
                        return Status.GREEN.toString();
                    } else {
                        return Status.RED.toString();
                    }
                }));
    }

    @Override
    public Map<String, String> getDetails() {
        return null;
    }

    public static List<K8sItem> getPodItems(KubernetesClient client) {
        var pods = client.pods().list().getItems();
        return pods.stream().map(pod -> {
            var podItem = new K8sItem(pod.getMetadata().getName(), pod.getMetadata().getUid(), ItemType.POD, new LevelDecorator(-1), new PodItem(pod));
            pod.getStatus().getConditions().forEach(condition -> podItem.addStatus(condition.getType(), condition.getStatus()));
            return podItem;
        }).collect(Collectors.toList());
    }
}
