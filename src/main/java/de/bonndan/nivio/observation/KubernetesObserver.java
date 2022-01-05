package de.bonndan.nivio.observation;


import de.bonndan.nivio.input.SourceReference;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * The KubernetesObserver Class is intended to detect changes in the Kubernetes cluster.
 * To achieve this the class compares two HashSets containing Kubernetes Items with each other.
 * The run method is executed once every second
 */

public class KubernetesObserver implements InputFormatObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesObserver.class);

    private final ApplicationEventPublisher eventPublisher;
    @NonNull
    private final SourceReference sourceReference;
    private final KubernetesClient kubernetesClient;
    private final List<HasMetadata> eventUidList;

    public KubernetesObserver(@NonNull final SourceReference sourceReference,
                              @NonNull final ApplicationEventPublisher eventPublisher,
                              @NonNull final KubernetesClient kubernetesClient
    ) {
        this.sourceReference = sourceReference;
        this.kubernetesClient = kubernetesClient;
        this.eventPublisher = eventPublisher;
        this.eventUidList = getK8sComponents();
    }

    @Override
    public void run() {
        if (!new HashSet<>(eventUidList).equals(new HashSet<>(getK8sComponents()))) {
            LOGGER.info("K8s change detected");
            eventPublisher.publishEvent(new InputChangedEvent(sourceReference.getUrl(), new ObservedChange("k8s cluster changed")));
        }
    }

    @NonNull
    private List<HasMetadata> getK8sComponents() {
        try {
            List<HasMetadata> componentList = new ArrayList<>();
            componentList.addAll(kubernetesClient.apps().deployments().list().getItems());
            componentList.addAll(kubernetesClient.persistentVolumeClaims().list().getItems());
            componentList.addAll(kubernetesClient.persistentVolumes().list().getItems());
            componentList.addAll(kubernetesClient.pods().list().getItems());
            componentList.addAll(kubernetesClient.apps().replicaSets().list().getItems());
            componentList.addAll(kubernetesClient.services().list().getItems());
            componentList.addAll(kubernetesClient.apps().statefulSets().list().getItems());
            return componentList;
        } catch (KubernetesClientException n) {
            LOGGER.error("Kubernetes might not be available");
        }
        return new ArrayList<>();
    }
}
