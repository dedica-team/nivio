package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.input.SeedConfiguration;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.observation.InputFormatObserver;
import de.bonndan.nivio.observation.KubernetesObserver;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.ReplicaSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@EnableKubernetesMockClient(crud = true, https = false)
class InputFormatHandlerKubernetesTest {
    KubernetesClient kubernetesClient;
    InputFormatHandlerKubernetes inputFormatHandlerKubernetes;

    @BeforeEach
    void setUp() throws MalformedURLException {
        SourceReference sourceReference = new SourceReference(null, "k8s");
        sourceReference.setUrl(new URL("http://localhost:80?groupLabel=release&namespace=default"));
        inputFormatHandlerKubernetes = new InputFormatHandlerKubernetes(Optional.of(kubernetesClient));
    }

    @Test
    void getFormats() {
        assertThat(inputFormatHandlerKubernetes.getFormats()).containsAll(List.of("kubernetes", "k8s"));
    }

    @Test
    void applyData() throws MalformedURLException {
        var landscapeDescription = new LandscapeDescription("test");
        var sourceReference = new SourceReference(new URL("https://dedica.team"), "");
        sourceReference.setConfig(new SeedConfiguration("k8sLandscapeTest"));
        inputFormatHandlerKubernetes.applyData(sourceReference, landscapeDescription);
        assertThat(landscapeDescription.getItemDescriptions().size()).isZero();
        var identifierMap = setK8sTestEnvironment();

        //when
        landscapeDescription = new LandscapeDescription("test");
        inputFormatHandlerKubernetes.applyData(sourceReference, landscapeDescription);

        //then
        assertThat(landscapeDescription.getItemDescriptions().size()).isEqualTo(7);
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("deployment"), "deployment", ItemDescription.class).orElseThrow().getRelations().size()).isZero();
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("replicaSet"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo(identifierMap.get("deployment"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("replicaSet"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo(identifierMap.get("replicaSet"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("pod"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo(identifierMap.get("replicaSet"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("pod"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo(identifierMap.get("pod"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("service"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo(identifierMap.get("deployment"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("service"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo(identifierMap.get("service"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("persistentVolume"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo(identifierMap.get("persistentVolumeClaim"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("persistentVolume"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo(identifierMap.get("persistentVolume"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("persistentVolumeClaim"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo(identifierMap.get("pod"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("persistentVolumeClaim"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo(identifierMap.get("persistentVolumeClaim"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("persistentVolumeClaim"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getSource()).isEqualTo(identifierMap.get("pod"));
        assertThat(landscapeDescription.getIndexReadAccess().findOneByIdentifiers(identifierMap.get("persistentVolumeClaim"), "deployment", ItemDescription.class).orElseThrow().getRelations().stream().findFirst().orElseThrow().getTarget()).isEqualTo(identifierMap.get("persistentVolumeClaim"));
    }

    private Map<String, String> setK8sTestEnvironment() {
        var identifier = new HashMap<String, String>();
        var service = new ServiceBuilder()
                .withNewMetadata().withName("deployment").withCreationTimestamp("testCreation").withUid("serviceUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withClusterIP("testIP").withNewType("testType").withSessionAffinity("testSessionAffinity").endSpec()
                .build();
        kubernetesClient.services().create(service);
        identifier.put("service", kubernetesClient.services().list().getItems().get(0).getMetadata().getUid());

        var deployment = new DeploymentBuilder()
                .withNewMetadata().withName("deployment").withCreationTimestamp("testCreation").withUid("deploymentUid").withLabels(Map.of("testLabelKey", "testLabelValue", "1", "2")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withNewStrategy().withNewType("strategyType").endStrategy().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.apps().deployments().create(deployment);
        identifier.put("deployment", kubernetesClient.apps().deployments().list().getItems().get(0).getMetadata().getUid());

        var replicaSet = new ReplicaSetBuilder()
                .withNewMetadata().withName("replicaSet").withCreationTimestamp("testCreation").withUid("replicaSetUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid(identifier.get("deployment")).build()).withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build();
        kubernetesClient.apps().replicaSets().create(replicaSet);
        identifier.put("replicaSet", kubernetesClient.apps().replicaSets().list().getItems().get(0).getMetadata().getUid());

        var statefulSet = new StatefulSetBuilder()
                .withNewMetadata().withName("statefulSet").withCreationTimestamp("testCreation").withUid("statefulSetUid").withLabels(Map.of("testLabelKey", "testLabelValue", "1", "2")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withReplicas(1).endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().withReadyReplicas(1).endStatus()
                .build();
        kubernetesClient.apps().statefulSets().create(statefulSet);
        identifier.put("statefulSet", kubernetesClient.apps().statefulSets().list().getItems().get(0).getMetadata().getUid());

        var pod = new PodBuilder()
                .withNewMetadata().withName("pod").withCreationTimestamp("testCreation").withUid("podUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid(identifier.get("replicaSet")).build()).withNamespace("test").endMetadata()
                .withNewSpec().addNewVolume().withNewPersistentVolumeClaim().withNewClaimName("persistentVolumeClaim").endPersistentVolumeClaim().endVolume().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.pods().create(pod);
        identifier.put("pod", kubernetesClient.pods().list().getItems().get(0).getMetadata().getUid());

        var persistentVolumeClaim = new PersistentVolumeClaimBuilder()
                .withNewMetadata().withName("persistentVolumeClaim").withCreationTimestamp("testCreation").withUid("persistentVolumeClaimUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withStorageClassName("testStorageName").endSpec()
                .withNewStatus().endStatus()
                .build();
        kubernetesClient.persistentVolumeClaims().create(persistentVolumeClaim);
        identifier.put("persistentVolumeClaim", kubernetesClient.persistentVolumeClaims().list().getItems().get(0).getMetadata().getUid());

        var persistentVolume = new PersistentVolumeBuilder()
                .withNewMetadata().withName("persistentVolume").withCreationTimestamp("testCreation").withUid("persistentVolumeUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withStorageClassName("testStorageName").withPersistentVolumeReclaimPolicy("testReclaimPolicy").addNewAccessMode("testAccessMode").addToCapacity("testCapacity", new Quantity("8", "Gi")).withClaimRef(new ObjectReferenceBuilder().withUid(identifier.get("persistentVolumeClaim")).build()).endSpec()
                .withNewStatus().endStatus()
                .build();
        kubernetesClient.persistentVolumes().create(persistentVolume);
        identifier.put("persistentVolume", kubernetesClient.persistentVolumes().list().getItems().get(0).getMetadata().getUid());

        return identifier;
    }

    @Test
    void getObserver() {
        //given
        var observer = Mockito.mock(InputFormatObserver.class);
        var applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        var sourceReference = Mockito.mock(SourceReference.class);

        //when
        var observerClass = Objects.requireNonNull(inputFormatHandlerKubernetes.getObserver(observer, applicationEventPublisher, sourceReference)).getClass();

        //then
        assertThat(observerClass).isEqualTo(KubernetesObserver.class);
    }
}