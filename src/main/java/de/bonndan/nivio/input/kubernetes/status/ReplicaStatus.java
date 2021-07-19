package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.itemadapters.ItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.ReplicaSetItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.StatefulSetItemAdapter;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class ReplicaStatus implements Status {

    @Override
    public Map<String, String> getExtendedStatus(Map<String, String> statusMap, ItemAdapter itemAdapter) {
        Integer replicaCount;
        Integer replicaCountDesired;
        if (itemAdapter instanceof ReplicaSetItemAdapter) {
            var concreteItem = (ReplicaSetItemAdapter) itemAdapter;
            replicaCount = concreteItem.getReadyReplicas();
            replicaCountDesired = concreteItem.getReplicas();
        } else if (itemAdapter instanceof StatefulSetItemAdapter) {
            var concreteItem = (StatefulSetItemAdapter) itemAdapter;
            replicaCount = concreteItem.getReadyReplicas();
            replicaCountDesired = concreteItem.getReplicas();
        } else {
            return statusMap;
        }
        var message = String.format("condition.%s of %s Pods are ready", replicaCount, replicaCountDesired);
        if (replicaCount == null) {
            return Collections.singletonMap("condition.ReadyReplicas count was null", de.bonndan.nivio.assessment.Status.ORANGE.toString());
        } else if (replicaCountDesired == null) {
            return Collections.singletonMap("condition.Replicas count was null", de.bonndan.nivio.assessment.Status.ORANGE.toString());
        } else if (Objects.equals(replicaCount, replicaCountDesired)) {
            return Collections.singletonMap("condition.all pods are ready", de.bonndan.nivio.assessment.Status.GREEN.toString());
        } else if (replicaCount == 0) {
            return Collections.singletonMap(message, de.bonndan.nivio.assessment.Status.RED.toString());
        } else
            return Collections.singletonMap(message, de.bonndan.nivio.assessment.Status.YELLOW.toString());
    }
}
