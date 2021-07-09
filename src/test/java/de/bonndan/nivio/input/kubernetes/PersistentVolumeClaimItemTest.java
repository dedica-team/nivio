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
class PersistentVolumeClaimItemTest {
    PersistentVolumeClaim persistentVolumeClaim;
    PersistentVolumeClaimItem persistentVolumeClaimItem;
    static KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
        persistentVolumeClaim = new PersistentVolumeClaimBuilder()
                .withNewMetadata()
                .withName("test")
                .withNamespace("test")
                .withUid("1234")
                .withLabels(Map.of("release", "testgroup"))
                .endMetadata()
                .withSpec(new PersistentVolumeClaimSpecBuilder()
                        .withVolumeName("test")
                        .build())
                .withStatus(new PersistentVolumeClaimStatusBuilder()
                        .build())
                .build();
        kubernetesClient.persistentVolumeClaims().create(persistentVolumeClaim);

        persistentVolumeClaimItem = new PersistentVolumeClaimItem("test", "1234", ItemType.VOLUME, persistentVolumeClaim, new LevelDecorator(4));
    }

    @Test
    void testGetWrappedItem() {
        HasMetadata result = persistentVolumeClaimItem.getWrappedItem();
        assertThat(result).isEqualTo(persistentVolumeClaim);
    }

    @Test
    void testGetPersistentVolumeClaimItems() {
        List<Item> result = PersistentVolumeClaimItem.getPersistentVolumeClaimItems(kubernetesClient);
        assertThat(result.size()).isEqualTo(Collections.singletonList(new PersistentVolumeClaimItem("test", "1234", ItemType.VOLUME, persistentVolumeClaim, new LevelDecorator(4))).size());
        result.forEach(item -> assertThat(item).isEqualToComparingFieldByField(persistentVolumeClaimItem));
    }

    @Test
    void testAddOwner() {
        var owner = new DeploymentItem("test", "1234", ItemType.VOLUME, null, new LevelDecorator(4));
        persistentVolumeClaimItem.addOwner(owner);
        assertThat(persistentVolumeClaimItem.getOwner()).isEqualTo(Collections.singletonList(owner));
    }

    @Test
    void testAddOwnerNull() {
        assertThatThrownBy(() -> persistentVolumeClaimItem.addOwner(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddRelation() {
        var relation = new RelationDescription("source", "target");
        persistentVolumeClaimItem.addRelation(relation);
        assertThat(persistentVolumeClaimItem.getRelationDescriptionList()).isEqualTo(Collections.singletonList(relation));
    }

    @Test
    void testAddRelationNull() {
        assertThatThrownBy(() -> persistentVolumeClaimItem.addRelation(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddStatus() {
        persistentVolumeClaimItem.addStatus("key", "value");
        assertThat(persistentVolumeClaimItem.getStatus()).isEqualTo(Collections.singletonMap("key", "value"));
    }

    @Test
    void testAddStatusNull() {
        assertThatThrownBy(() -> persistentVolumeClaimItem.addStatus(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistentVolumeClaimItem.addStatus("null", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> persistentVolumeClaimItem.addStatus(null, "null")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGetGroup() {

        var owner = new DeploymentItem("test", "1234", ItemType.VOLUME, null, new LevelDecorator(4));
        persistentVolumeClaimItem.addOwner(owner);
        String result = persistentVolumeClaimItem.getGroup();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void testSetOwners() {
        List<Item> owner = Collections.singletonList(new DeploymentItem("test", "1234", ItemType.VOLUME, null, new LevelDecorator(4)));
        persistentVolumeClaimItem.setOwners(owner);
        assertThat(persistentVolumeClaimItem.getOwner()).isEqualTo(owner);
    }

    @Test
    void testSetOwnersNull() {
        assertThatThrownBy(() -> persistentVolumeClaimItem.setOwners(null)).isInstanceOf(NullPointerException.class);
    }
}
