package de.bonndan.nivio.input.kubernetes;

import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.observation.InputChangedEvent;
import de.bonndan.nivio.observation.KubernetesObserver;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@EnableKubernetesMockClient(crud = true, https = false)
class KubernetesObserverTest {

    ApplicationEventPublisher eventPublisher;
    KubernetesClient kubernetesClient;
    KubernetesObserver kubernetesObserver;
    Landscape landscape;

    @BeforeEach
    void setUp() {
        landscape = LandscapeFactory.createForTesting("foo", "bar").build();
        eventPublisher = mock(ApplicationEventPublisher.class);
        kubernetesObserver = new KubernetesObserver(landscape, eventPublisher, kubernetesClient);
    }

    @Test
    void run() {
        var deployment = new DeploymentBuilder()
                .withNewMetadata().withName("deployment").withCreationTimestamp("testCreation").withUid("testUid").withLabels(Map.of("testLabelKey", "testLabelValue")).withOwnerReferences(new OwnerReferenceBuilder().withUid("testOwnerUid").build()).withNamespace("test").endMetadata()
                .withNewSpec().withNewStrategy().withNewType("strategyType").endStrategy().endSpec()
                .withNewStatus().addNewCondition().withType("testType").withStatus("testStatus").endCondition().endStatus()
                .build();
        kubernetesClient.apps().deployments().create(deployment);
        kubernetesObserver.run();

        verify(eventPublisher).publishEvent(any(InputChangedEvent.class));
    }
}