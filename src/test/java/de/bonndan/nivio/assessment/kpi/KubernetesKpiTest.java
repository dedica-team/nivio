package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KubernetesKpiTest {
    KubernetesKPI kubernetesKPI;

    @BeforeEach
    void setUp() {
        kubernetesKPI = new KubernetesKPI();
    }

    @Test
    void getStatusValues() {
        assertThat(kubernetesKPI.getStatusValues(Mockito.mock(Assessable.class)).getClass()).isEqualTo(ArrayList.class);
        var item = new Item("test", new Landscape("test", Map.of(), "test", null, null, null, null, null, null, Map.of()), "test", null, null, null, null, null, null, null);
        item.setLabel("k8s.boolcondition.test", "true");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.GREEN);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("test");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
        item = new Item("test", new Landscape("test", Map.of(), "test", null, null, null, null, null, null, Map.of()), "test", null, null, null, null, null, null, null);
        item.setLabel("k8s.boolcondition.test", "false");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.RED);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("test");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");

        item = new Item("test", new Landscape("test", Map.of(), "test", null, null, null, null, null, null, Map.of()), "test", null, null, null, null, null, null, null);
        item.setLabel("k8s.replicacondition.test", "a;1");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.ORANGE);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("ReadyReplicas count was null");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
        item = new Item("test", new Landscape("test", Map.of(), "test", null, null, null, null, null, null, Map.of()), "test", null, null, null, null, null, null, null);
        item.setLabel("k8s.replicacondition.test", "1;a");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.ORANGE);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("Replicas count was null");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");

        item = new Item("test", new Landscape("test", Map.of(), "test", null, null, null, null, null, null, Map.of()), "test", null, null, null, null, null, null, null);
        item.setLabel("k8s.replicacondition.test", "2;2");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.GREEN);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("all pods are ready");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
        item = new Item("test", new Landscape("test", Map.of(), "test", null, null, null, null, null, null, Map.of()), "test", null, null, null, null, null, null, null);
        item.setLabel("k8s.replicacondition.test", "1;2");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.YELLOW);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("1 of 2 Pods are ready");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
        item = new Item("test", new Landscape("test", Map.of(), "test", null, null, null, null, null, null, Map.of()), "test", null, null, null, null, null, null, null);
        item.setLabel("k8s.replicacondition.test", "0;2");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.RED);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("0 of 2 Pods are ready");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");

        item = new Item("test", new Landscape("test", Map.of(), "test", null, null, null, null, null, null, Map.of()), "test", null, null, null, null, null, null, null);
        item.setLabel("k8s.testcondition.test", Status.GREEN.toString());
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.GREEN);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("testcondition.test");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void testGetSetEnabled() {
        kubernetesKPI.setEnabled(false);
        assertThat(kubernetesKPI.isEnabled()).isFalse();
    }
}
