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
class ServiceItemTest {
    Service service;
    ServiceItem serviceItem;
    static KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
        service = new ServiceBuilder()
                .withNewMetadata()
                .withName("test")
                .withNamespace("test")
                .withUid("1234")
                .withLabels(Map.of("release", "testgroup"))
                .endMetadata()
                .withSpec(new ServiceSpecBuilder()
                        .build())
                .withStatus(new ServiceStatusBuilder()
                        .build())
                .build();
        kubernetesClient.services().create(service);

        serviceItem = new ServiceItem("test", "1234", ItemType.SERVICE, service, new LevelDecorator(4));
    }

    @Test
    void testGetWrappedItem() {
        HasMetadata result = serviceItem.getWrappedItem();
        assertThat(result).isEqualTo(service);
    }

    @Test
    void testGetServiceItems() {
        List<Item> result = ServiceItem.getServiceItems(kubernetesClient);
        assertThat(result.size()).isEqualTo(Collections.singletonList(new ServiceItem("test", "1234", ItemType.SERVICE, service, new LevelDecorator(4))).size());
        result.forEach(item -> assertThat(item).isEqualToComparingFieldByField(serviceItem));
    }

    @Test
    void testAddOwner() {
        var owner = new DeploymentItem("test", "1234", ItemType.SERVICE, null, new LevelDecorator(4));
        serviceItem.addOwner(owner);
        assertThat(serviceItem.getOwner()).isEqualTo(Collections.singletonList(owner));
    }

    @Test
    void testAddOwnerNull() {
        assertThatThrownBy(() -> serviceItem.addOwner(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddRelation() {
        var relation = new RelationDescription("source", "target");
        serviceItem.addRelation(relation);
        assertThat(serviceItem.getRelationDescriptionList()).isEqualTo(Collections.singletonList(relation));
    }

    @Test
    void testAddRelationNull() {
        assertThatThrownBy(() -> serviceItem.addRelation(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testAddStatusStatus() {
        serviceItem.addStatus("key", "value");
        assertThat(serviceItem.getStatus()).isEqualTo(Collections.singletonMap("key", "value"));
    }

    @Test
    void testAddStatusNull() {
        assertThatThrownBy(() -> serviceItem.addStatus(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> serviceItem.addStatus("null", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> serviceItem.addStatus(null, "null")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGetGroup() {
        var owner = new DeploymentItem("test", "1234", ItemType.DEPLOYMENT, null, new LevelDecorator(4));
        serviceItem.addOwner(owner);
        String result = serviceItem.getGroup();
        assertThat(result).isEqualTo("test");
    }

    @Test
    void testSetOwners() {
        List<Item> owner = Collections.singletonList(new DeploymentItem("test", "1234", ItemType.SERVICE, null, new LevelDecorator(4)));
        serviceItem.setOwners(owner);
        assertThat(serviceItem.getOwner()).isEqualTo(owner);
    }

    @Test
    void testSetOwnersNull() {
        assertThatThrownBy(() -> serviceItem.setOwners(null)).isInstanceOf(NullPointerException.class);
    }
}