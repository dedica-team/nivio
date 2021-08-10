package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@EnableKubernetesMockClient(crud = true, https = false)
class InputFormatHandlerKubernetesTest {
    KubernetesClient kubernetesClient;
    InputFormatHandlerKubernetes inputFormatHandlerKubernetes;

    @BeforeEach
    void setUp() {
        SourceReference sourceReference = new SourceReference(null, "k8s");
        sourceReference.setUrl("http://localhost:80?groupLabel=release&namespace=default");
        inputFormatHandlerKubernetes = new InputFormatHandlerKubernetes(Optional.of(kubernetesClient));
    }

    @Test
    void getFormats() {
        assertThat(inputFormatHandlerKubernetes.getFormats()).containsAll(List.of("kubernetes", "k8s"));
    }

    @Test
    void applyData() throws MalformedURLException {
        var landscapeDescription = new LandscapeDescription("k8sLandscapeTest");
        var url = new URL("https://dedica.team");
        var sourceReference = new SourceReference("https://dedica.team", "");
        inputFormatHandlerKubernetes.applyData(sourceReference, url, landscapeDescription);
        assertThat(landscapeDescription.getItemDescriptions().all().size()).isZero();
        setK8sTestEnvironment();
        inputFormatHandlerKubernetes.applyData(sourceReference, url, landscapeDescription);
        assertThat(landscapeDescription.getItemDescriptions().all().size()).isEqualTo(7);
        assertThat(landscapeDescription.getItemDescriptions().find("deploymentUid", "deployment").orElseThrow().getRelations().size()).isZero();
        assertThat(landscapeDescription.getItemDescriptions().find("replicaSetUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo("deploymentUid");
        assertThat(landscapeDescription.getItemDescriptions().find("replicaSetUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo("replicaSetUid");
        assertThat(landscapeDescription.getItemDescriptions().find("podUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo("replicaSetUid");
        assertThat(landscapeDescription.getItemDescriptions().find("podUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo("podUid");
        assertThat(landscapeDescription.getItemDescriptions().find("serviceUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo("deploymentUid");
        assertThat(landscapeDescription.getItemDescriptions().find("serviceUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo("serviceUid");
        assertThat(landscapeDescription.getItemDescriptions().find("persistentVolumeUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo("persistentVolumeClaimUid");
        assertThat(landscapeDescription.getItemDescriptions().find("persistentVolumeUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo("persistentVolumeUid");
        assertThat(landscapeDescription.getItemDescriptions().find("persistentVolumeClaimUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo("podUid");
        assertThat(landscapeDescription.getItemDescriptions().find("persistentVolumeClaimUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo("persistentVolumeClaimUid");
        assertThat(landscapeDescription.getItemDescriptions().find("persistentVolumeClaimUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo("podUid");
        assertThat(landscapeDescription.getItemDescriptions().find("persistentVolumeClaimUid", "deployment").orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo("persistentVolumeClaimUid");
    }

    private void setK8sTestEnvironment() {
        var service = new ServiceBuilder()
                .withNewMetadata().withName("deployment").withCreationTimestamp("testCreation").withUid("serviceUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withClusterIP("testIP").withNewType("testType").withSessionAffinity("testSessionAffinity").endSpec()
                .build();
        kubernetesClient.services().create(service);
        var deployment = new DeploymentBuilder()
                .withNewMetadata().withName("deployment").withCreationTimestamp("testCreation").withUid("deploymentUid").withLabels(Map.of("testLabelKey", "testLabelValue", "1", "2")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withNewStrategy().withNewType("strategyType").endStrategy().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.apps().deployments().create(deployment);
        var replicaSet = new ReplicaSetBuilder()
                .withNewMetadata().withName("replicaSet").withCreationTimestamp("testCreation").withUid("replicaSetUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("deploymentUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build();
        kubernetesClient.apps().replicaSets().create(replicaSet);
        var statefulSet = new StatefulSetBuilder()
                .withNewMetadata().withName("statefulSet").withCreationTimestamp("testCreation").withUid("statefulSetUid").withLabels(Map.of("testLabelKey", "testLabelValue", "1", "2")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build();
        kubernetesClient.apps().statefulSets().create(statefulSet);
        var pod = new PodBuilder()
                .withNewMetadata().withName("pod").withCreationTimestamp("testCreation").withUid("podUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("replicaSetUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().addNewVolume().withNewPersistentVolumeClaim().withNewClaimName("persistentvolumeclaim").endPersistentVolumeClaim().endVolume().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.pods().create(pod);
        var persistentVolumeClaim = new PersistentVolumeClaimBuilder()
                .withNewMetadata().withName("persistentvolumeclaim").withCreationTimestamp("testCreation").withUid("persistentVolumeClaimUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withStorageClassName("testStorageName").endSpec()
                .withNewStatus().withNewPhase("testPhase").endStatus()
                .build();
        kubernetesClient.persistentVolumeClaims().create(persistentVolumeClaim);
        var persistentVolume = new PersistentVolumeBuilder()
                .withNewMetadata().withName("persistentvolume").withCreationTimestamp("testCreation").withUid("persistentVolumeUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withStorageClassName("testStorageName").withPersistentVolumeReclaimPolicy("testReclaimPolicy").addNewAccessMode("testAccessMode").addToCapacity("testCapacity", new Quantity("8", "Gi")).withClaimRef(new ObjectReferenceBuilder().withUid("persistentVolumeClaimUid").build()).endSpec()
                .withNewStatus().withNewPhase("testPhase").endStatus()
                .build();
        kubernetesClient.persistentVolumes().create(persistentVolume);
    }

    @Test
    void getObserver() {
        assertNull(inputFormatHandlerKubernetes.getObserver(Mockito.mock(InputFormatObserver.class), Mockito.mock(SourceReference.class)));
    }
}