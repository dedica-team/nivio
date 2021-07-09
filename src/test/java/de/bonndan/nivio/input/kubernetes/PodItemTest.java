package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.dto.RelationDescription;
import io.fabric8.kubernetes.api.model.*;
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
class PodItemTest {
    Pod pod;
    PodItem podItem;
    static KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
        pod = new PodBuilder()
                .withNewMetadata()
                .withName("test")
                .withNamespace("test")
                .withUid("1234")
                .withLabels(Map.of("release", "testgroup"))
                .endMetadata()
                .withSpec(new PodSpecBuilder()
                        .build())
                .withStatus(new PodStatusBuilder()
                        .build())
                .build();
        kubernetesClient.pods().create(pod);

        podItem = new PodItem("test", "1234", ItemType.POD, pod, new LevelDecorator(4));
    }

    @Test
    void testGetWrappedItem() {
        HasMetadata result = podItem.getWrappedItem();
        assertThat(result).isEqualTo(pod);
    }

    @Test
    void testGetPodItems() {
        List<Item> result = PodItem.getPodItems(kubernetesClient);
        assertThat(result.size()).isEqualTo(Collections.singletonList(new PodItem("test", "1234", ItemType.POD, pod, new LevelDecorator(4))).size());
        result.forEach(item -> assertThat(item).isEqualToComparingFieldByField(podItem));
    }

    @Test
    void testAddOwner() {
        var owner = new DeploymentItem("test", "1234", ItemType.POD, null, new LevelDecorator(4));
        podItem.addOwner(owner);
        assertThat(podItem.getOwner()).isEqualTo(Collections.singletonList(owner));
    }

    @Test
    void testAddOwnerNull() {
        assertThatThrownBy(() -> podItem.addOwner(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddRelation() {
        var relation = new RelationDescription("source", "target");
        podItem.addRelation(relation);
        assertThat(podItem.getRelationDescriptionList()).isEqualTo(Collections.singletonList(relation));
    }

    @Test
    void testAddRelationNull() {
        assertThatThrownBy(() -> podItem.addRelation(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddStatusGreen() {
        podItem.addStatus("key", "true");
        assertThat(podItem.getStatus()).isEqualTo(Collections.singletonMap("key", "green"));
    }

    @Test
    void testAddStatusRed() {
        podItem.addStatus("key", "false");
        assertThat(podItem.getStatus()).isEqualTo(Collections.singletonMap("key", "red"));
    }

    @Test
    void testAddStatusNull() {
        assertThatThrownBy(() -> podItem.addStatus(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> podItem.addStatus("null", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> podItem.addStatus(null, "null")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGetGroup() {
        var owner = new DeploymentItem("test", "1234", ItemType.POD, null, new LevelDecorator(4));
        podItem.addOwner(owner);
        String result = podItem.getGroup();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void testSetOwners() {
        List<Item> owner = Collections.singletonList(new DeploymentItem("test", "1234", ItemType.POD, null, new LevelDecorator(4)));
        podItem.setOwners(owner);
        assertThat(podItem.getOwner()).isEqualTo(owner);
    }

    @Test
    void testSetOwnersNull() {
        assertThatThrownBy(() -> podItem.setOwners(null)).isInstanceOf(NullPointerException.class);
    }
}