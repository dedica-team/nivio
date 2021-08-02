package de.bonndan.nivio.input.kubernetes.status;

import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.kubernetes.KubernetesObserver;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.observation.LandscapeObserverPool;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KubernetesObserverRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesObserverRegistry.class);

    private final Map<String, LandscapeObserverPool> observerMap = new ConcurrentHashMap<>();

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ThreadPoolTaskScheduler taskScheduler;

    public KubernetesObserverRegistry(ThreadPoolTaskScheduler taskScheduler, ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.taskScheduler = taskScheduler;
    }

    @EventListener(ProcessingFinishedEvent.class)
    public void onProcessingFinishedEvent(ProcessingFinishedEvent event) {
        LandscapeDescription landscapeDescription = event.getInput();
        Landscape landscape = Objects.requireNonNull(event.getLandscape());

        if (landscapeDescription == null) {
            String msg = String.format("No landscape description (input) available. Landscape %s could not be registered for observation", landscape.getIdentifier());
            landscape.getLog().warn(msg);
            LOGGER.warn(msg);
            return;
        }

        LandscapeObserverPool pool = observerMap.computeIfAbsent(landscape.getIdentifier(), s -> {
            LOGGER.info("Registered landscape {} for observation.", landscapeDescription);
            return new LandscapeObserverPool(taskScheduler, 30 * 1000);
        });
        pool.updateObservers(List.of(new KubernetesObserver(landscapeDescription, applicationEventPublisher, new DefaultKubernetesClient())));
    }
}
