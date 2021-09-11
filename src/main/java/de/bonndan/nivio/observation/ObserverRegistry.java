package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.SeedConfigurationProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to register landscapes to observe description source changes.
 */
@Service
public class ObserverRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverRegistry.class);

    private final Map<URL, ObserverPool> observerMap = new ConcurrentHashMap<>();

    private final ObserverFactory observerPoolFactory;
    private final ThreadPoolTaskScheduler taskScheduler;

    public ObserverRegistry(ObserverFactory observerPoolFactory,
                            ThreadPoolTaskScheduler taskScheduler
    ) {
        this.observerPoolFactory = observerPoolFactory;
        this.taskScheduler = taskScheduler;
    }

    /**
     * Landscape are registered for observation here.
     *
     * On processing success, {@link ProcessingFinishedEvent} is fired and read here to register the landscape.
     */
    @EventListener(SeedConfigurationProcessedEvent.class)
    public void onProcessingFinishedEvent(SeedConfigurationProcessedEvent event) {
        event.getSource().getSource().getURL().ifPresent(url -> {
            ObserverPool pool = observerMap.computeIfAbsent(url, url1 -> {
                LOGGER.info("Registered seed config {} for observation.", url1);
                return new ObserverPool(taskScheduler, 30 * 1000);
            });
            pool.updateObservers(observerPoolFactory.getObserversFor(event.getSource()));
        });
    }

    Set<URL> getObserved() {
        return observerMap.keySet();
    }
}
