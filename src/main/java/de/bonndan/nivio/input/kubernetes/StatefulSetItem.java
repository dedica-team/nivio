package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatefulSetItem implements Item {
    private final StatefulSet statefulSet;

    public StatefulSetItem(StatefulSet statefulSet) {
        this.statefulSet = statefulSet;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return statefulSet;
    }

    @Override
    public Map<String, String> getStatus(Map<String, String> status) {
        return null;
    }

    @Override
    public Map<String, String> getDetails() {
        return null;
    }

    public static List<K8sItem> getStatefulSetItems(KubernetesClient client) {
        var statefulSetList = client.apps().statefulSets().list().getItems();
        return statefulSetList.stream().map(statefulSet -> new K8sItem(statefulSet.getMetadata().getName(), statefulSet.getMetadata().getUid(), ItemType.STATEFULSET, new LevelDecorator(3), new StatefulSetItem(statefulSet))).collect(Collectors.toList());
    }
}
