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

public class PodItem extends Item {
    private final Pod pod;

    protected PodItem(String name, String uid, String type, Pod pod, LevelDecorator levelDecorator) {
        super(name, uid, type, levelDecorator);
        this.pod = pod;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return pod;
    }

    @Override
    @NonNull
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

    public static List<Item> getPodItems(KubernetesClient client) {
        var pods = client.pods().list().getItems();
        return pods.stream().map(pod -> {
            var podItem = new PodItem(pod.getMetadata().getName(), pod.getMetadata().getUid(), ItemType.POD, pod, new LevelDecorator(2));
            pod.getStatus().getConditions().forEach(condition -> podItem.addStatus(condition.getType(), condition.getStatus()));
            return podItem;
        }).collect(Collectors.toList());
    }
}
