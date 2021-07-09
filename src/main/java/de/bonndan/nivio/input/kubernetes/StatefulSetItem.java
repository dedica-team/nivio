package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class StatefulSetItem extends Item {
    private final StatefulSet statefulSet;

    protected StatefulSetItem(String name, String uid, String type, StatefulSet statefulSet, LevelDecorator levelDecorator) {
        super(name, uid, type, levelDecorator);
        this.statefulSet = statefulSet;
    }

    @Override
    @NonNull
    public HasMetadata getWrappedItem() {
        return statefulSet;
    }

    public static List<Item> getStatefulSetItems(KubernetesClient client) {
        var statefulSetList = client.apps().statefulSets().list().getItems();
        return statefulSetList.stream().map(statefulSet -> new StatefulSetItem(statefulSet.getMetadata().getName(), statefulSet.getMetadata().getUid(), ItemType.STATEFULSET, statefulSet, new LevelDecorator(3))).collect(Collectors.toList());
    }
}
