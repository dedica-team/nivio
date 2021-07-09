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
class PersistentVolumeItemTest {
    PersistentVolume persistentVolume;
    PersistentVolumeItem persistentVolumeItem;
    static KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
        persistentVolume = new PersistentVolumeBuilder()
                .withNewMetadata()
                .withName("test")
                .withNamespace("test")
                .withUid("1234")
                .withLabels(Map.of("release", "testgroup"))
                .endMetadata()
                .withSpec(new PersistentVolumeSpecBuilder()
                        .build())
                .withStatus(new PersistentVolumeStatusBuilder()
                        .build())
                .build();
        kubernetesClient.persistentVolumes().create(persistentVolume);

        persistentVolumeItem = new PersistentVolumeItem("test", "1234", ItemType.VOLUME, persistentVolume, new LevelDecorator(4));
    }

    @Test
    void testGetWrappedItem() {
        HasMetadata result = persistentVolumeItem.getWrappedItem();
        assertThat(result).isEqualTo(persistentVolume);
    }

    @Test
    void testGetPersistentVolumeItems() {
        List<Item> result = PersistentVolumeItem.getPersistentVolumeItems(kubernetesClient);
        assertThat(result.size()).isEqualTo(Collections.singletonList(new PersistentVolumeItem("test", "1234", ItemType.VOLUME, persistentVolume, new LevelDecorator(4))).size());
        result.forEach(item -> assertThat(item).isEqualToComparingFieldByField(persistentVolumeItem));
    }

    @Test
    void testAddOwner() {
        var owner = new DeploymentItem("test", "1234", ItemType.VOLUME, null, new LevelDecorator(4));
        persistentVolumeItem.addOwner(owner);
        assertThat(persistentVolumeItem.getOwner()).isEqualTo(Collections.singletonList(owner));
    }

    @Test
    void testAddOwnerNull() {
        assertThatThrownBy(() -> persistentVolumeItem.addOwner(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddRelation() {
        var relation = new RelationDescription("source", "target");
        persistentVolumeItem.addRelation(relation);
        assertThat(persistentVolumeItem.getRelationDescriptionList()).isEqualTo(Collections.singletonList(relation));
    }

    @Test
    void testAddRelationNull() {
        assertThatThrownBy(() -> persistentVolumeItem.addRelation(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddStatus() {
        persistentVolumeItem.addStatus("key", "value");
        assertThat(persistentVolumeItem.getStatus()).isEqualTo(Collections.singletonMap("key", "value"));
    }

    @Test
    void testAddStatusNull() {
        assertThatThrownBy(() -> persistentVolumeItem.addStatus(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistentVolumeItem.addStatus("null", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistentVolumeItem.addStatus(null, "null")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGetGroup() {
        var owner = new DeploymentItem("test", "1234", ItemType.VOLUME, null, new LevelDecorator(4));
        persistentVolumeItem.addOwner(owner);
        String result = persistentVolumeItem.getGroup();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void testSetOwners() {
        List<Item> owner = Collections.singletonList(new DeploymentItem("test", "1234", ItemType.VOLUME, null, new LevelDecorator(4)));
        persistentVolumeItem.setOwners(owner);
        assertThat(persistentVolumeItem.getOwner()).isEqualTo(owner);
    }

    @Test
    void testSetOwnersNull() {
        assertThatThrownBy(() -> persistentVolumeItem.setOwners(null)).isInstanceOf(NullPointerException.class);
    }
}
