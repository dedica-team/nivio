package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.items.Item;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class ReplicaStatus implements Status {

    @Override
    public Map<String, String> getExtendedStatus(Map<String, String> statusMap, Item item) {
        Integer replicaCount;
        Integer replicaCountDesired;
        if (item.getWrappedItem() instanceof ReplicaSet) {
            var concreteItem = (ReplicaSet) item.getWrappedItem();
            replicaCount = concreteItem.getStatus().getReadyReplicas();
            replicaCountDesired = concreteItem.getSpec().getReplicas();
        } else if (item.getWrappedItem() instanceof StatefulSet) {
            var concreteItem = (StatefulSet) item.getWrappedItem();
            replicaCount = concreteItem.getStatus().getReadyReplicas();
            replicaCountDesired = concreteItem.getSpec().getReplicas();
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
