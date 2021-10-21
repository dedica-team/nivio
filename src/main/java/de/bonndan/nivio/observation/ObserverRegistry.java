package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.SeedConfigurationProcessedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to register landscapes to observe description source changes.
 */
@Service
public class ObserverRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverRegistry.class);

    private final Map<String, ObserverPool> observerMap = new ConcurrentHashMap<>();

    private final ObserverFactory observerPoolFactory;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final ObserverConfigProperties observerConfigProperties;

    public ObserverRegistry(ObserverFactory observerPoolFactory,
                            ThreadPoolTaskScheduler taskScheduler,
                            ObserverConfigProperties observerConfigProperties
    ) {
        this.observerPoolFactory = observerPoolFactory;
        this.taskScheduler = taskScheduler;
        this.observerConfigProperties = observerConfigProperties;
    }

    /**
     * Landscape are registered for observation here.
     *
     * On processing success, {@link ProcessingFinishedEvent} is fired and read here to register the landscape.
     */
    @EventListener(SeedConfigurationProcessedEvent.class)
    public void onProcessingFinishedEvent(SeedConfigurationProcessedEvent event) {
        event.getSource().getSource().getURL().ifPresent(url -> {
            ObserverPool pool = observerMap.computeIfAbsent(url.toString(), url1 -> {
                LOGGER.info("Registered seed config {} for observation.", url1);
                return new ObserverPool(taskScheduler, observerConfigProperties);
            });
            pool.updateObservers(observerPoolFactory.getObserversFor(event.getSource()));
        });
    }

    Set<String> getObserved() {
        return observerMap.keySet();
    }

}
