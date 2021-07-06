package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.dto.RelationDescription;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@EnableKubernetesMockClient(crud = true, https = false)
class PersistentVolumeClaimItemTest {
    PersistentVolumeClaim persistentVolumeClaim;
    PersistentVolumeClaimItem persistentVolumeClaimItem;
    KubernetesMockServer kubernetesMockServer = new KubernetesMockServer();
    static KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
        persistentVolumeClaim = new PersistentVolumeClaimBuilder()
                .withNewMetadata()
                .withName("test")
                .withNamespace("default")
                .withLabels(Map.of("release", "testgroup"))
                .endMetadata()
                .withSpec(new PersistentVolumeClaimSpecBuilder()
                        .withVolumeName("test")
                        .build())
                .withStatus(new PersistentVolumeClaimStatusBuilder()
                        .build())
                .build();
        persistentVolumeClaimItem = new PersistentVolumeClaimItem("test", "1234", ItemType.VOLUME, persistentVolumeClaim);
        kubernetesClient.persistentVolumeClaims().inNamespace("default").create(persistentVolumeClaim);
    }

    @Test
    void testGetWrappedItem() {
        HasMetadata result = persistentVolumeClaimItem.getWrappedItem();
        assertThat(result).isEqualTo(persistentVolumeClaim);
    }

    @Test
    void testGetPersistentVolumeClaimItems() {

        var test = kubernetesClient.persistentVolumeClaims().list().getItems();
        List<PersistentVolumeClaimItem> result = PersistentVolumeClaimItem.getPersistentVolumeClaimItems(kubernetesClient);
        assertThat(Collections.singletonList(new PersistentVolumeClaimItem("test", "1234", ItemType.VOLUME, persistentVolumeClaim))).isEqualTo(result);
    }

    @Test
    void testAddOwner() {
        persistentVolumeClaimItem.addOwner(null);
    }

    @Test
    void testAddRelation() {
        persistentVolumeClaimItem.addRelation(new RelationDescription("source", "target"));
    }

    @Test
    void testAddStatus() {
        persistentVolumeClaimItem.addStatus("key", "value");
    }

    @Test
    void testGetGroup() {
        String result = persistentVolumeClaimItem.getGroup();
        Assertions.assertEquals("replaceMeWithExpectedResult", result);
    }
}
