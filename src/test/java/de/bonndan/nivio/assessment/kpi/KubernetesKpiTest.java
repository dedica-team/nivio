package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.kubernetes.InputFormatHandlerKubernetes;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.ItemFactory;
import de.bonndan.nivio.model.Landscape;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KubernetesKpiTest {
    KubernetesKPI kubernetesKPI;
    private Item item;

    @BeforeEach
    void setUp() {
        kubernetesKPI = new KubernetesKPI();
        item = ItemFactory.getTestItem("test", "test");
    }

    @Test
    void boolTrue() {
        assertThat(kubernetesKPI.getStatusValues(Mockito.mock(Assessable.class)).getClass()).isEqualTo(ArrayList.class);
        item.setLabel(InputFormatHandlerKubernetes.LABEL_PREFIX + ".boolcondition.test", "true");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.GREEN);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("test");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void boolFalse() {
        item.setLabel(InputFormatHandlerKubernetes.LABEL_PREFIX + ".boolcondition.test", "false");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.RED);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("test");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void replicaa1() {
        item.setLabel(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.test", "a;1");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.ORANGE);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("ReadyReplicas count was null");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void replica1a() {
        item.setLabel(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.test", "1;a");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.ORANGE);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("Replicas count was null");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void replica22() {
        item.setLabel(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.test", "2;2");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.GREEN);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("all pods are ready");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void replica12() {
        item.setLabel(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.test", "1;2");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.YELLOW);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("1 of 2 Pods are ready");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void replica02() {
        item.setLabel(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.test", "0;2");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getStatus()).isEqualTo(Status.RED);
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getMessage()).isEqualTo("0 of 2 Pods are ready");
        assertThat(kubernetesKPI.getStatusValues(item).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void testcondition() {
        item.setLabel(InputFormatHandlerKubernetes.LABEL_PREFIX + ".testcondition.test", Status.GREEN.toString());
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