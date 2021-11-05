package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.ReplicaSetItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.StatefulSetItemAdapter;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReplicaStatusTest {

    @Test
    void getExtendedStatusReplicaSet() {
        var replicaStatus = new ReplicaStatus();
        var itemAdapter = Mockito.mock(ReplicaSetItemAdapter.class);
        Mockito.when(itemAdapter.getReadyReplicas()).thenReturn(1);
        Mockito.when(itemAdapter.getReplicas()).thenReturn(1);
        var extendedStatus = replicaStatus.getExtendedStatus(Map.of("testKey", "testValue"), itemAdapter);
        assertThat(extendedStatus).isEqualTo(Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.replicas", "1;1"));
    }

    @Test
    void getExtendedStatusStatefulSet() {
        var replicaStatus = new ReplicaStatus();
        var itemAdapter = Mockito.mock(StatefulSetItemAdapter.class);
        Mockito.when(itemAdapter.getReadyReplicas()).thenReturn(1);
        Mockito.when(itemAdapter.getReplicas()).thenReturn(1);
        var extendedStatus = replicaStatus.getExtendedStatus(Map.of("testKey", "testValue"), itemAdapter);
        assertThat(extendedStatus).isEqualTo(Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.replicas", "1;1"));
    }

    @Test
    void getExtendedStatusItemAdapter() {
        var replicaStatus = new ReplicaStatus();
        var itemAdapter = new DeploymentItemAdapter(new Deployment());
        var extendedStatus = replicaStatus.getExtendedStatus(Map.of("testKey", "testValue"), itemAdapter);
        assertThat(extendedStatus).isEqualTo(Map.of("testKey", "testValue"));
    }
}