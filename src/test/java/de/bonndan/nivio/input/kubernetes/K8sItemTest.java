package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.kubernetes.itemadapters.PodItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.ReplicaSetItemAdapter;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class K8sItemTest {
    K8sItem k8sItem;

    @BeforeEach
    void setUp() {
        k8sItem = new K8sItemBuilder(ItemType.POD, new PodItemAdapter(new PodBuilder()
                .withNewMetadata().withName("pod").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build())).build();
    }

    @Test
    void addGetOwner() {
        assertThat(k8sItem.getOwner().size()).isZero();
        var ownerK8sItem = new K8sItemBuilder(ItemType.REPLICASET, new ReplicaSetItemAdapter(new ReplicaSetBuilder()
                .withNewMetadata().withName("replicaSet").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build())).build();
        k8sItem.addOwner(ownerK8sItem);
        assertThat(k8sItem.getOwner().size()).isOne();
        assertThat(k8sItem.getOwner()).containsExactly(ownerK8sItem);
    }

    @Test
    void addGetRelation() {
        assertThat(k8sItem.getRelationDescriptionList().size()).isZero();
        var relationDescription = new RelationDescription("abc1", "abc2");
        k8sItem.addRelation(relationDescription);
        assertThat(k8sItem.getRelationDescriptionList().size()).isOne();
        assertThat(k8sItem.getRelationDescriptionList()).containsExactly(relationDescription);
    }

    @Test
    void getGroup() {
        assertThat(k8sItem.getGroup()).isEqualTo("pod");
        var ownerK8sItem = new K8sItemBuilder(ItemType.REPLICASET, new ReplicaSetItemAdapter(new ReplicaSetBuilder()
                .withNewMetadata().withName("replicaSet").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build())).build();
        k8sItem.addOwner(ownerK8sItem);
        assertThat(k8sItem.getGroup()).isEqualTo("replicaSet");
    }

    @Test
    void getLevelDecorator() {
        assertThat(k8sItem.getLevelDecorator()).isEqualToComparingFieldByField(new LevelDecorator(K8sJsonParser.getExperimentalLevel(k8sItem.getItemAdapter().getClass())));
    }

    @Test
    void getName() {
        assertThat(k8sItem.getName()).isEqualTo("pod");
    }
}
