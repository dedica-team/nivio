package de.bonndan.nivio.input.kubernetes;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.observation.InputChangedEvent;
import de.bonndan.nivio.observation.KubernetesObserver;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@EnableKubernetesMockClient(crud = true, https = false)
class KubernetesObserverTest {

    private ListAppender<ILoggingEvent> logWatcher;
    ApplicationEventPublisher eventPublisher;
    KubernetesClient kubernetesClient;
    KubernetesObserver kubernetesObserver;
    SourceReference sourceReference;

    @BeforeEach
    void setUp() throws MalformedURLException {
        sourceReference = new SourceReference(new URL("http://foo.com"));
        eventPublisher = mock(ApplicationEventPublisher.class);
    }

    @Test
    void testExceptionHandling() {
        //given
        this.logWatcher = new ListAppender<>();
        this.logWatcher.start();
        ((Logger) LoggerFactory.getLogger(KubernetesObserver.class)).addAppender(this.logWatcher);
        var kubernetesClientException = Mockito.mock(KubernetesClient.class);
        Mockito.when(kubernetesClientException.apps()).thenThrow(KubernetesClientException.class);

        //when
        kubernetesObserver = new KubernetesObserver(sourceReference, eventPublisher, kubernetesClientException);

        //then
        int logSize = logWatcher.list.size();
        assertThat(logWatcher.list.get(logSize - 1).getFormattedMessage()).contains("Kubernetes might not be available");
        verify(eventPublisher, never()).publishEvent(any(InputChangedEvent.class));
    }

    @Test
    void run() {
        kubernetesObserver = new KubernetesObserver(sourceReference, eventPublisher, kubernetesClient);
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