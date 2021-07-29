package de.bonndan.nivio.assessment.kpi;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class KubernetesKpiTest {
    KubernetesKPI kubernetesKPI;

    @BeforeEach
    void setUp() {
        kubernetesKPI = new KubernetesKPI();
    }

    @Test
    void getStatusValues() {
        assertThat(kubernetesKPI.getStatusValues(Mockito.mock(Component.class)).getClass()).isEqualTo(ArrayList.class);
        var itemDescription = new ItemDescription();
        itemDescription.setLabel("k8s.boolcondition.test", "true");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getStatus()).isEqualTo(Status.GREEN);
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getMessage()).isEqualTo("test");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getField()).isEqualTo("k8s:0");
        itemDescription = new ItemDescription();
        itemDescription.setLabel("k8s.boolcondition.test", "false");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getStatus()).isEqualTo(Status.RED);
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getMessage()).isEqualTo("test");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getField()).isEqualTo("k8s:0");

        itemDescription = new ItemDescription();
        itemDescription.setLabel("k8s.replicacondition.test", "a;1");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getStatus()).isEqualTo(Status.ORANGE);
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getMessage()).isEqualTo("ReadyReplicas count was null");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getField()).isEqualTo("k8s:0");
        itemDescription = new ItemDescription();
        itemDescription.setLabel("k8s.replicacondition.test", "1;a");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getStatus()).isEqualTo(Status.ORANGE);
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getMessage()).isEqualTo("Replicas count was null");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getField()).isEqualTo("k8s:0");

        itemDescription = new ItemDescription();
        itemDescription.setLabel("k8s.replicacondition.test", "2;2");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getStatus()).isEqualTo(Status.GREEN);
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getMessage()).isEqualTo("all pods are ready");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getField()).isEqualTo("k8s:0");
        itemDescription = new ItemDescription();
        itemDescription.setLabel("k8s.replicacondition.test", "1;2");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getStatus()).isEqualTo(Status.YELLOW);
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getMessage()).isEqualTo("1 of 2 Pods are ready");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getField()).isEqualTo("k8s:0");
        itemDescription = new ItemDescription();
        itemDescription.setLabel("k8s.replicacondition.test", "0;2");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getStatus()).isEqualTo(Status.RED);
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getMessage()).isEqualTo("0 of 2 Pods are ready");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getField()).isEqualTo("k8s:0");

        itemDescription = new ItemDescription();
        itemDescription.setLabel("k8s.testcondition.test", Status.GREEN.toString());
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getStatus()).isEqualTo(Status.GREEN);
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getMessage()).isEqualTo("testcondition.test");
        assertThat(kubernetesKPI.getStatusValues(itemDescription).get(0).getField()).isEqualTo("k8s:0");
    }

    @Test
    void testGetSetEnabled() {
        kubernetesKPI.setEnabled(false);
        assertThat(kubernetesKPI.isEnabled()).isFalse();
    }
}
