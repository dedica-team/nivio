package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.ItemType;
import de.bonndan.nivio.input.kubernetes.itemadapters.*;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@EnableKubernetesMockClient(crud = true, https = false)
class CreateItemsTest {
    KubernetesClient kubernetesClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getDeploymentItems() {
        var deployment = new DeploymentBuilder()
                .withNewMetadata().withName("deployment").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().withNewStrategy().withNewType("strategyType").endStrategy().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.apps().deployments().create(deployment);
        var deploymentList = CreateItems.getDeploymentItems(kubernetesClient);
        assertThat(deploymentList).isNotNull();
        assertThat(deploymentList.size()).isEqualTo(1);
        assertThat(deploymentList.get(0).getUid()).isEqualTo("testUid");
        assertThat(deploymentList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(deploymentList.get(0).getDetails()).isEqualTo(Map.of("creation", "testCreation", "k8s.boolcondition.testtype", "teststatus", "name", "deployment", "namespace", "test", "strategy", "strategyType"));
        assertThat(deploymentList.get(0).getItemAdapter()).isEqualToComparingFieldByField(new DeploymentItemAdapter(deployment));
        assertThat(deploymentList.get(0).getType()).isEqualTo(ItemType.DEPLOYMENT);
    }

    @Test
    void getPersistentVolumeClaimItems() {
        var persistentVolumeClaim = new PersistentVolumeClaimBuilder()
                .withNewMetadata().withName("persistentvolumeclaim").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().withStorageClassName("testStorageName").endSpec()
                .withNewStatus().withNewPhase("testPhase").endStatus()
                .build();
        kubernetesClient.persistentVolumeClaims().create(persistentVolumeClaim);
        var persistentVolumeClaimList = CreateItems.getPersistentVolumeClaimItems(kubernetesClient);
        assertThat(persistentVolumeClaimList).isNotNull();
        assertThat(persistentVolumeClaimList.size()).isEqualTo(1);
        assertThat(persistentVolumeClaimList.get(0).getUid()).isEqualTo("testUid");
        assertThat(persistentVolumeClaimList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(persistentVolumeClaimList.get(0).getDetails()).isEqualTo(Map.of("creation", "testCreation", "name", "persistentvolumeclaim", "namespace", "test", "phase status", "testPhase", "storage class", "testStorageName"));
        assertThat(persistentVolumeClaimList.get(0).getItemAdapter()).isEqualToComparingFieldByField(new PersistentVolumeClaimItemAdapter(persistentVolumeClaim));
        assertThat(persistentVolumeClaimList.get(0).getType()).isEqualTo(ItemType.VOLUME);
    }

    @Test
    void getPersistentVolumeItems() {
        var persistentVolume = new PersistentVolumeBuilder()
                .withNewMetadata().withName("persistentvolume").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().withStorageClassName("testStorageName").withPersistentVolumeReclaimPolicy("testReclaimPolicy").addNewAccessMode("testAccessMode").addToCapacity("testCapacity", new Quantity("8", "Gi")).endSpec()
                .withNewStatus().withNewPhase("testPhase").endStatus()
                .build();
        kubernetesClient.persistentVolumes().create(persistentVolume);
        var persistentVolumeList = CreateItems.getPersistentVolumeItems(kubernetesClient);
        assertThat(persistentVolumeList).isNotNull();
        assertThat(persistentVolumeList.size()).isEqualTo(1);
        assertThat(persistentVolumeList.get(0).getUid()).isEqualTo("testUid");
        assertThat(persistentVolumeList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(persistentVolumeList.get(0).getDetails()).isEqualTo(Map.of("creation", "testCreation", "name", "persistentvolume", "namespace", "test", "phase status", "testPhase", "reclaim policy", "testReclaimPolicy", "storage class", "testStorageName", "storage mode", "testAccessMode", "testCapacity", "8Gi"));
        assertThat(persistentVolumeList.get(0).getItemAdapter().getWrappedItem()).isEqualTo(persistentVolume);
        assertThat(persistentVolumeList.get(0).getType()).isEqualTo(ItemType.VOLUME);
    }

    @Test
    void getPodItems() {
        var pod = new PodBuilder()
                .withNewMetadata().withName("pod").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.pods().create(pod);
        var podList = CreateItems.getPodItems(kubernetesClient);
        assertThat(podList).isNotNull();
        assertThat(podList.size()).isEqualTo(1);
        assertThat(podList.get(0).getUid()).isEqualTo("testUid");
        assertThat(podList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(podList.get(0).getDetails()).isEqualTo(Map.of("creation", "testCreation", "k8s.boolcondition.testtype", "teststatus", "name", "pod", "namespace", "test"));
        assertThat(podList.get(0).getItemAdapter()).isEqualToComparingFieldByField(new PodItemAdapter(pod));
        assertThat(podList.get(0).getType()).isEqualTo(ItemType.POD);
    }

    @Test
    void getReplicaSetItems() {
        var replicaSet = new ReplicaSetBuilder()
                .withNewMetadata().withName("replicaSet").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build();
        kubernetesClient.apps().replicaSets().create(replicaSet);
        var replicaSetList = CreateItems.getReplicaSetItems(kubernetesClient);
        assertThat(replicaSetList).isNotNull();
        assertThat(replicaSetList.size()).isEqualTo(1);
        assertThat(replicaSetList.get(0).getUid()).isEqualTo("testUid");
        assertThat(replicaSetList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(replicaSetList.get(0).getDetails()).isEqualTo(Map.of("creation", "testCreation", "k8s.replicacondition.replicas", "1;1", "name", "replicaSet", "namespace", "test"));
        assertThat(replicaSetList.get(0).getItemAdapter()).isEqualToComparingFieldByField(new ReplicaSetItemAdapter(replicaSet));
        assertThat(replicaSetList.get(0).getType()).isEqualTo(ItemType.REPLICASET);
    }

    @Test
    void getServiceItems() {
        var service = new ServiceBuilder()
                .withNewMetadata().withName("service").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().withClusterIP("testIP").withNewType("testType").withSessionAffinity("testSessionAffinity").endSpec()
                .build();
        kubernetesClient.services().create(service);
        var serviceList = CreateItems.getServiceItems(kubernetesClient);
        assertThat(serviceList).isNotNull();
        assertThat(serviceList.size()).isEqualTo(1);
        assertThat(serviceList.get(0).getUid()).isEqualTo("testUid");
        assertThat(serviceList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(serviceList.get(0).getDetails()).isEqualTo(Map.of("cluster ip", "testIP", "creation", "testCreation", "name", "service", "namespace", "test", "service type", "testType", "session affinity", "testSessionAffinity"));
        assertThat(serviceList.get(0).getItemAdapter()).isEqualToComparingFieldByField(new ServiceItemAdapter(service));
        assertThat(serviceList.get(0).getType()).isEqualTo(ItemType.SERVICE);
    }

    @Test
    void getStatefulSetItems() {
        var statefulSet = new StatefulSetBuilder()
                .withNewMetadata().withName("statefulSet").withCreationTimestamp("testCreation").withUid("testUid").withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build();
        kubernetesClient.apps().statefulSets().create(statefulSet);
        var statefulSetList = CreateItems.getStatefulSetItems(kubernetesClient);
        assertThat(statefulSetList).isNotNull();
        assertThat(statefulSetList.size()).isEqualTo(1);
        assertThat(statefulSetList.get(0).getUid()).isEqualTo("testUid");
        assertThat(statefulSetList.get(0).getClass()).isEqualTo(K8sItem.class);
        assertThat(statefulSetList.get(0).getDetails()).isEqualTo(Map.of("creation", "testCreation", "k8s.replicacondition.replicas", "1;1", "name", "statefulSet", "namespace", "test"));
        assertThat(statefulSetList.get(0).getItemAdapter()).isEqualToComparingFieldByField(new StatefulSetItemAdapter(statefulSet));
        assertThat(statefulSetList.get(0).getType()).isEqualTo(ItemType.STATEFULSET);
    }
}
