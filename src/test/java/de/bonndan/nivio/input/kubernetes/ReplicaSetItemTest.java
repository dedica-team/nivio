package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.dto.RelationDescription;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetSpecBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetStatusBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnableKubernetesMockClient(crud = true, https = false)
class ReplicaSetItemTest {
    ReplicaSet replicaSet;
    ReplicaSetItem replicaSetItem;
    static KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
        replicaSet = new ReplicaSetBuilder()
                .withNewMetadata()
                .withName("test")
                .withNamespace("test")
                .withUid("1234")
                .withLabels(Map.of("release", "testgroup"))
                .withAnnotations(new HashMap<>())
                .endMetadata()
                .withSpec(new ReplicaSetSpecBuilder()
                        .build())
                .withStatus(new ReplicaSetStatusBuilder()
                        .build())
                .build();
        kubernetesClient.apps().replicaSets().create(replicaSet);

        replicaSetItem = new ReplicaSetItem("test", "1234", ItemType.REPLICASET, replicaSet, new LevelDecorator(4));
    }

    @Test
    void testGetWrappedItem() {
        HasMetadata result = replicaSetItem.getWrappedItem();
        assertThat(result).isEqualTo(replicaSet);
    }

    @Test
    void testGetReplicaSetItems() {
        List<Item> result = ReplicaSetItem.getReplicaSetItems(kubernetesClient);
        assertThat(result.size()).isEqualTo(Collections.singletonList(new ReplicaSetItem("test", "1234", ItemType.REPLICASET, replicaSet, new LevelDecorator(4))).size());
        result.forEach(item -> assertThat(item).isEqualToComparingFieldByField(replicaSetItem));
    }

    @Test
    void testAddOwner() {
        var owner = new DeploymentItem("test", "1234", ItemType.REPLICASET, null, new LevelDecorator(4));
        replicaSetItem.addOwner(owner);
        assertThat(replicaSetItem.getOwner()).isEqualTo(Collections.singletonList(owner));
    }

    @Test
    void testAddOwnerNull() {
        assertThatThrownBy(() -> replicaSetItem.addOwner(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddRelation() {
        var relation = new RelationDescription("source", "target");
        replicaSetItem.addRelation(relation);
        assertThat(replicaSetItem.getRelationDescriptionList()).isEqualTo(Collections.singletonList(relation));
    }

    @Test
    void testAddRelationNull() {
        assertThatThrownBy(() -> replicaSetItem.addRelation(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddStatusGreen() {
        ((ReplicaSet) replicaSetItem.getWrappedItem()).getSpec().setReplicas(4);
        replicaSetItem.getWrappedItem().getMetadata().getAnnotations().putIfAbsent("deployment.kubernetes.io/desired-replicas", "4");
        assertThat(replicaSetItem.getStatus()).isEqualTo(Collections.singletonMap("4 of 4 Pods are ready", "green"));
    }

    @Test
    void testAddStatusOrange() {
        ((ReplicaSet) replicaSetItem.getWrappedItem()).getSpec().setReplicas(2);
        replicaSetItem.getWrappedItem().getMetadata().getAnnotations().putIfAbsent("deployment.kubernetes.io/desired-replicas", "4");
        assertThat(replicaSetItem.getStatus()).isEqualTo(Collections.singletonMap("2 of 4 Pods are ready", "orange"));
    }

    @Test
    void testAddStatusRed() {
        ((ReplicaSet) replicaSetItem.getWrappedItem()).getSpec().setReplicas(0);
        replicaSetItem.getWrappedItem().getMetadata().getAnnotations().putIfAbsent("deployment.kubernetes.io/desired-replicas", "4");
        assertThat(replicaSetItem.getStatus()).isEqualTo(Collections.singletonMap("0 of 4 Pods are ready", "red"));
    }

    @Test
    void testAddStatusNull() {
        assertThatThrownBy(() -> replicaSetItem.addStatus(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> replicaSetItem.addStatus("null", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> replicaSetItem.addStatus(null, "null")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGetGroup() {
        var owner = new DeploymentItem("test", "1234", ItemType.DEPLOYMENT, null, new LevelDecorator(4));
        replicaSetItem.addOwner(owner);
        String result = replicaSetItem.getGroup();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void testSetOwners() {
        List<Item> owner = Collections.singletonList(new DeploymentItem("test", "1234", ItemType.REPLICASET, null, new LevelDecorator(4)));
        replicaSetItem.setOwners(owner);
        assertThat(replicaSetItem.getOwner()).isEqualTo(owner);
    }

    @Test
    void testSetOwnersNull() {
        assertThatThrownBy(() -> replicaSetItem.setOwners(null)).isInstanceOf(NullPointerException.class);
    }
}