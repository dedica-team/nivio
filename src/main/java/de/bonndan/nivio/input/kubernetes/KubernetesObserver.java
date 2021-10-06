package de.bonndan.nivio.input.kubernetes;


import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.observation.InputChangedEvent;
import de.bonndan.nivio.observation.InputFormatObserver;
import de.bonndan.nivio.observation.ObservedChange;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class KubernetesObserver implements InputFormatObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesObserver.class);

    private final Landscape landscape;
    private final ApplicationEventPublisher eventPublisher;
    private final KubernetesClient kubernetesClient;
    private final List<Long> eventUidList;

    public KubernetesObserver(@NonNull final Landscape landscape,
                              @NonNull final ApplicationEventPublisher eventPublisher,
                              @NonNull final KubernetesClient kubernetesClient) {
        this.landscape = landscape;
        this.kubernetesClient = kubernetesClient;
        this.eventPublisher = eventPublisher;
        this.eventUidList = getK8sComponents();
    }

    @Override
    public void run() {
        var change = false;
        while (!change) {
            sleep(1000);
            if ((eventUidList.stream().mapToLong(Long::longValue).sum() - getK8sComponents().stream().mapToLong(Long::longValue).sum()) != 0) {
                change = true;
                LOGGER.info("K8s change detected");
                eventPublisher.publishEvent(new InputChangedEvent(new ObservedChange(landscape, "k8s cluster changed")));
            }
        }

    }

    @NonNull
    private List<Long> getK8sComponents() {
        try {
            var componentList = kubernetesClient.apps().deployments().list().getItems().stream().map(deployment -> (long) deployment.hashCode()).collect(Collectors.toList());
            componentList.addAll(kubernetesClient.persistentVolumeClaims().list().getItems().stream().map(persistentVolumeClaim -> (long) persistentVolumeClaim.hashCode()).collect(Collectors.toList()));
            componentList.addAll(kubernetesClient.persistentVolumes().list().getItems().stream().map(persistentVolume -> (long) persistentVolume.hashCode()).collect(Collectors.toList()));
            componentList.addAll(kubernetesClient.pods().list().getItems().stream().map(pod -> (long) pod.hashCode()).collect(Collectors.toList()));
            componentList.addAll(kubernetesClient.apps().replicaSets().list().getItems().stream().map(replicaSet -> (long) replicaSet.hashCode()).collect(Collectors.toList()));
            componentList.addAll(kubernetesClient.services().list().getItems().stream().map(service -> (long) service.hashCode()).collect(Collectors.toList()));
            componentList.addAll(kubernetesClient.apps().statefulSets().list().getItems().stream().map(statefulSet -> (long) statefulSet.hashCode()).collect(Collectors.toList()));
            return componentList;
        } catch (KubernetesClientException n) {
            LOGGER.error(n.getMessage());
            LOGGER.error("Kubernetes might not be available");
        }
        return new ArrayList<>();
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
