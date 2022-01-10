package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.itemadapters.DeploymentItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.PersistentVolumeItemAdapter;
import de.bonndan.nivio.input.kubernetes.itemadapters.PodItemAdapter;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;


@EnableKubernetesMockClient(crud = true, https = false)
class CreateItemsTest {
    KubernetesClient kubernetesClient;

    @Test
    void getDeploymentItems() {
        var deployment = new DeploymentBuilder()
                .withNewMetadata().withName("deployment").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withNewStrategy().withNewType("strategyType").endStrategy().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.apps().deployments().create(deployment);
        var deploymentList = CreateItems.getDeploymentItems(kubernetesClient);
        assertThat(deploymentList).isNotNull();
        assertThat(deploymentList.size()).isOne();
        assertThat(deploymentList.get(0).getItemAdapter().getLabels()).containsExactly(entry("testLabelKey", "testLabelValue"));
        assertThat(deploymentList.get(0).getItemAdapter().getOwnerReferences().size()).isOne();
        assertThat(deploymentList.get(0).getItemAdapter().getOwnerReferences().get(0).getUid()).isEqualTo("testOwnerUid");
        assertThat(deploymentList.get(0).getUid()).isNotEmpty();
        assertThat(deploymentList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(deploymentList.get(0).getDetails().get("creation")).isNotEmpty();
        assertThat(deploymentList.get(0).getDetails()).containsEntry(InputFormatHandlerKubernetes.LABEL_PREFIX + ".boolcondition.testtype", "teststatus");
        assertThat(deploymentList.get(0).getItemAdapter().getName()).isEqualTo(new DeploymentItemAdapter(deployment).getName());
        assertThat(deploymentList.get(0).getType()).isEqualTo(ItemType.DEPLOYMENT);
    }

    @Test
    void getPersistentVolumeClaimItems() {
        var persistentVolumeClaim = new PersistentVolumeClaimBuilder()
                .withNewMetadata().withCreationTimestamp("").withName("persistentvolumeclaim").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withStorageClassName("testStorageName").endSpec()
                .withNewStatus().withNewPhase("testPhase").endStatus()
                .build();
        kubernetesClient.persistentVolumeClaims().create(persistentVolumeClaim);
        var persistentVolumeClaimList = CreateItems.getPersistentVolumeClaimItems(kubernetesClient);
        assertThat(persistentVolumeClaimList).isNotNull();
        assertThat(persistentVolumeClaimList.size()).isOne();
        assertThat(persistentVolumeClaimList.get(0).getItemAdapter().getLabels()).containsExactly(entry("testLabelKey", "testLabelValue"));
        assertThat(persistentVolumeClaimList.get(0).getItemAdapter().getOwnerReferences().size()).isOne();
        assertThat(persistentVolumeClaimList.get(0).getItemAdapter().getOwnerReferences().get(0).getUid()).isEqualTo("testOwnerUid");
        assertThat(persistentVolumeClaimList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(persistentVolumeClaimList.get(0).getType()).isEqualTo(ItemType.VOLUME);
        var testDetails = persistentVolumeClaimList.get(0).getDetails();
        testDetails.remove("creation");
        assertThat(testDetails).isEqualTo(Map.of("name", "persistentvolumeclaim", "namespace", "test", "phase status", "testPhase", "storage class", "testStorageName"));
    }

    @Test
    void getPersistentVolumeItems() {
        var persistentVolume = new PersistentVolumeBuilder()
                .withNewMetadata().withName("persistentvolume").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withStorageClassName("testStorageName").withPersistentVolumeReclaimPolicy("testReclaimPolicy").addNewAccessMode("testAccessMode").addToCapacity("testCapacity", new Quantity("8", "Gi")).withClaimRef(new ObjectReferenceBuilder().withUid("testClaimUid").build()).endSpec()
                .withNewStatus().withNewPhase("testPhase").endStatus()
                .build();
        kubernetesClient.persistentVolumes().create(persistentVolume);
        var persistentVolumeList = CreateItems.getPersistentVolumeItems(kubernetesClient);
        assertThat(persistentVolumeList).isNotNull();
        assertThat(persistentVolumeList.size()).isOne();
        assertThat(persistentVolumeList.get(0).getItemAdapter().getLabels()).containsExactly(entry("testLabelKey", "testLabelValue"));
        assertThat(persistentVolumeList.get(0).getItemAdapter().getOwnerReferences().size()).isOne();
        assertThat(persistentVolumeList.get(0).getItemAdapter().getOwnerReferences().get(0).getUid()).isEqualTo("testOwnerUid");
        assertThat(persistentVolumeList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(persistentVolumeList.get(0).getType()).isEqualTo(ItemType.VOLUME);
        assertThat(((PersistentVolumeItemAdapter) persistentVolumeList.get(0).getItemAdapter()).getClaimRef().getUid()).isEqualTo("testClaimUid");
        var testDetails = persistentVolumeList.get(0).getDetails();
        testDetails.remove("creation");
        assertThat(testDetails).isEqualTo(Map.of("name", "persistentvolume", "namespace", "test", "phase status", "testPhase", "reclaim policy", "testReclaimPolicy", "storage class", "testStorageName", "storage mode", "testAccessMode", "testCapacity", "8Gi"));
    }

    @Test
    void getPodItems() {
        var pod = new PodBuilder()
                .withNewMetadata().withName("pod").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().addNewVolume().withNewPersistentVolumeClaim().withNewClaimName("testClaimName").endPersistentVolumeClaim().endVolume().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.pods().create(pod);
        var podList = CreateItems.getPodItems(kubernetesClient);
        assertThat(podList).isNotNull();
        assertThat(podList.size()).isOne();
        assertThat(podList.get(0).getItemAdapter().getLabels()).containsExactly(entry("testLabelKey", "testLabelValue"));
        assertThat(podList.get(0).getItemAdapter().getOwnerReferences().size()).isOne();
        assertThat(podList.get(0).getItemAdapter().getOwnerReferences().get(0).getUid()).isEqualTo("testOwnerUid");
        assertThat(podList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(((PodItemAdapter) podList.get(0).getItemAdapter()).getVolumes().size()).isOne();
        assertThat(((PodItemAdapter) podList.get(0).getItemAdapter()).getVolumes().get(0).getPersistentVolumeClaim().getClaimName()).isEqualTo("testClaimName");
        assertThat(podList.get(0).getType()).isEqualTo(ItemType.POD);
        var testDetails = podList.get(0).getDetails();
        testDetails.remove("creation");
        assertThat(testDetails).isEqualTo(Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".boolcondition.testtype", "teststatus", "name", "pod", "namespace", "test"));
    }

    @Test
    void getReplicaSetItems() {
        var replicaSet = new ReplicaSetBuilder()
                .withNewMetadata().withName("replicaSet").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build();
        kubernetesClient.apps().replicaSets().create(replicaSet);
        var replicaSetList = CreateItems.getReplicaSetItems(kubernetesClient);
        assertThat(replicaSetList).isNotNull();
        assertThat(replicaSetList.size()).isOne();
        assertThat(replicaSetList.get(0).getItemAdapter().getLabels()).containsExactly(entry("testLabelKey", "testLabelValue"));
        assertThat(replicaSetList.get(0).getItemAdapter().getOwnerReferences().size()).isOne();
        assertThat(replicaSetList.get(0).getItemAdapter().getOwnerReferences().get(0).getUid()).isEqualTo("testOwnerUid");
        assertThat(replicaSetList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(replicaSetList.get(0).getType()).isEqualTo(ItemType.REPLICASET);
        var testDetails = replicaSetList.get(0).getDetails();
        testDetails.remove("creation");
        assertThat(testDetails).isEqualTo(Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.replicas", "1;1", "name", "replicaSet", "namespace", "test"));
    }

    @Test
    void getServiceItems() {
        var service = new ServiceBuilder()
                .withNewMetadata().withName("service").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withClusterIP("testIP").withNewType("testType").withSessionAffinity("testSessionAffinity").endSpec()
                .build();
        kubernetesClient.services().create(service);
        var serviceList = CreateItems.getServiceItems(kubernetesClient);
        assertThat(serviceList).isNotNull();
        assertThat(serviceList.size()).isOne();
        assertThat(serviceList.get(0).getItemAdapter().getLabels()).containsExactly(entry("testLabelKey", "testLabelValue"));
        assertThat(serviceList.get(0).getItemAdapter().getOwnerReferences().size()).isOne();
        assertThat(serviceList.get(0).getItemAdapter().getOwnerReferences().get(0).getUid()).isEqualTo("testOwnerUid");
        assertThat(serviceList.get(0).getClass()).isEqualTo(K8sItem.class);
        ;
        assertThat(serviceList.get(0).getType()).isEqualTo(ItemType.SERVICE);
        var testDetails = serviceList.get(0).getDetails();
        testDetails.remove("creation");
        assertThat(testDetails).isEqualTo(Map.of("cluster ip", "testIP", "name", "service", "namespace", "test", "service type", "testType", "session affinity", "testSessionAffinity"));
    }

    @Test
    void getStatefulSetItems() {
        var statefulSet = new StatefulSetBuilder()
                .withNewMetadata().withName("statefulSet").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build();
        kubernetesClient.apps().statefulSets().create(statefulSet);
        var statefulSetList = CreateItems.getStatefulSetItems(kubernetesClient);
        assertThat(statefulSetList).isNotNull();
        assertThat(statefulSetList.size()).isOne();
        assertThat(statefulSetList.get(0).getItemAdapter().getLabels()).containsExactly(entry("testLabelKey", "testLabelValue"));
        assertThat(statefulSetList.get(0).getItemAdapter().getOwnerReferences().size()).isOne();
        assertThat(statefulSetList.get(0).getItemAdapter().getOwnerReferences().get(0).getUid()).isEqualTo("testOwnerUid");
        assertThat(statefulSetList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(statefulSetList.get(0).getType()).isEqualTo(ItemType.STATEFULSET);
        var testDetails = statefulSetList.get(0).getDetails();
        testDetails.remove("creation");
        assertThat(testDetails).isEqualTo(Map.of(InputFormatHandlerKubernetes.LABEL_PREFIX + ".replicacondition.replicas", "1;1", "name", "statefulSet", "namespace", "test"));
    }
}
