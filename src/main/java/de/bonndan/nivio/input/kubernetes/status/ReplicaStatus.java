package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.ReplicaSetItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.StatefulSetItemAdapter;
import org.springframework.lang.NonNull;

import java.util.Map;

public class ReplicaStatus implements Status {

    @Override
    public Map<String, String> getExtendedStatus(@NonNull Map<String, String> statusMap, @NonNull ItemAdapter itemAdapter) {
        if (itemAdapter instanceof ReplicaSetItemAdapter) {
            var concreteItem = (ReplicaSetItemAdapter) itemAdapter;
            return Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.replicas", concreteItem.getReadyReplicas() + ";" + concreteItem.getReplicas());
        } else if (itemAdapter instanceof StatefulSetItemAdapter) {
            var concreteItem = (StatefulSetItemAdapter) itemAdapter;
            return Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.replicas", concreteItem.getReadyReplicas() + ";" + concreteItem.getReplicas());
        } else {
            return statusMap;
        }
    }
}
