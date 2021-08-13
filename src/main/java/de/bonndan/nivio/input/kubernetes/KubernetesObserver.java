package de.bonndan.nivio.input.kubernetes;


import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.observation.InputChangedEvent;
import de.bonndan.nivio.observation.InputFormatObserver;
import de.bonndan.nivio.observation.ObservedChange;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.stream.Collectors;


public class KubernetesObserver implements InputFormatObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesObserver.class);

    private final Landscape landscape;
    private final ApplicationEventPublisher eventPublisher;
    private final KubernetesClient kubernetesClient;
    private List<Event> eventList = null;

    public KubernetesObserver(@NonNull final Landscape landscape,
                              @NonNull final ApplicationEventPublisher eventPublisher,
                              @NonNull final KubernetesClient kubernetesClient) {
        this.landscape = landscape;
        this.kubernetesClient = kubernetesClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void run() {
        var change = false;
        while (!change) {
            if (eventList == null) {
                eventList = kubernetesClient.events().v1().events().list().getItems();
            } else {
                if (!compareEvents(eventList, kubernetesClient.events().v1().events().list().getItems())) {
                    triggerChange();
                    change = true;
                }
            }
        }
    }

    private boolean compareEvents(@NonNull List<Event> eventListOld, @NonNull List<Event> eventListNew) {
        var eventListOldName = eventListOld.stream().map(event -> event.getMetadata().getName()).collect(Collectors.toList());
        var eventListNewName = eventListNew.stream().map(event -> event.getMetadata().getName()).collect(Collectors.toList());
        return eventListNewName.equals(eventListOldName);
    }

    private void triggerChange() {
        LOGGER.info("Kubernetes Observer published new IndexEvent");
        eventPublisher.publishEvent(new InputChangedEvent(new ObservedChange(landscape, "k8s cluster changed")));
    }
}
