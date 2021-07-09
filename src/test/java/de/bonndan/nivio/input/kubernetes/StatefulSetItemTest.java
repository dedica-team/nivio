package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.dto.RelationDescription;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetSpecBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetStatusBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnableKubernetesMockClient(crud = true, https = false)
class StatefulSetItemTest {
    StatefulSet stateful;
    StatefulSetItem statefulItem;
    static KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
        stateful = new StatefulSetBuilder()
                .withNewMetadata()
                .withName("test")
                .withNamespace("test")
                .withUid("1234")
                .withLabels(Map.of("release", "testgroup"))
                .endMetadata()
                .withSpec(new StatefulSetSpecBuilder()
                        .build())
                .withStatus(new StatefulSetStatusBuilder()
                        .build())
                .build();
        kubernetesClient.apps().statefulSets().create(stateful);

        statefulItem = new StatefulSetItem("test", "1234", ItemType.STATEFULSET, stateful, new LevelDecorator(4));
    }

    @Test
    void testGetWrappedItem() {
        HasMetadata result = statefulItem.getWrappedItem();
        assertThat(result).isEqualTo(stateful);
    }

    @Test
    void testGetStatefulSetItems() {
        List<Item> result = StatefulSetItem.getStatefulSetItems(kubernetesClient);
        assertThat(result.size()).isEqualTo(Collections.singletonList(new StatefulSetItem("test", "1234", ItemType.STATEFULSET, stateful, new LevelDecorator(4))).size());
        result.forEach(item -> assertThat(item).isEqualToComparingFieldByField(statefulItem));
    }

    @Test
    void testAddOwner() {
        var owner = new DeploymentItem("test", "1234", ItemType.STATEFULSET, null, new LevelDecorator(4));
        statefulItem.addOwner(owner);
        assertThat(statefulItem.getOwner()).isEqualTo(Collections.singletonList(owner));
    }

    @Test
    void testAddOwnerNull() {
        assertThatThrownBy(() -> statefulItem.addOwner(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddRelation() {
        var relation = new RelationDescription("source", "target");
        statefulItem.addRelation(relation);
        assertThat(statefulItem.getRelationDescriptionList()).isEqualTo(Collections.singletonList(relation));
    }

    @Test
    void testAddRelationNull() {
        assertThatThrownBy(() -> statefulItem.addRelation(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddStatus() {
        statefulItem.addStatus("key", "value");
        assertThat(statefulItem.getStatus()).isEqualTo(Collections.singletonMap("key", "value"));
    }

    @Test
    void testAddStatusNull() {
        assertThatThrownBy(() -> statefulItem.addStatus(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> statefulItem.addStatus("null", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> statefulItem.addStatus(null, "null")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGetGroup() {
        var owner = new DeploymentItem("test", "1234", ItemType.DEPLOYMENT, null, new LevelDecorator(4));
        statefulItem.addOwner(owner);
        String result = statefulItem.getGroup();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void testSetOwners() {
        List<Item> owner = Collections.singletonList(new DeploymentItem("test", "1234", ItemType.STATEFULSET, null, new LevelDecorator(4)));
        statefulItem.setOwners(owner);
        assertThat(statefulItem.getOwner()).isEqualTo(owner);
    }

    @Test
    void testSetOwnersNull() {
        assertThatThrownBy(() -> statefulItem.setOwners(null)).isInstanceOf(NullPointerException.class);
    }
}