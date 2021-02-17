package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.IndexEvent;
import de.bonndan.nivio.input.ProcessingException;
import de.bonndan.nivio.input.ProcessingFinishedEvent;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service to register landscapes to observe description source changes.
 */
@Service
public class ObserverRegistry implements ApplicationListener<ProcessingFinishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverRegistry.class);

    private final Map<String, LandscapeObserverPool> observerMap = new ConcurrentHashMap<>();

    private final LandscapeObserverPoolFactory landscapeObserverPoolFactory;
    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ApplicationEventPublisher publisher;

    public ObserverRegistry(LandscapeObserverPoolFactory landscapeObserverPoolFactory,
                            LandscapeDescriptionFactory landscapeDescriptionFactory,
                            ApplicationEventPublisher publisher
    ) {
        this.landscapeObserverPoolFactory = landscapeObserverPoolFactory;
        this.landscapeDescriptionFactory = landscapeDescriptionFactory;
        this.publisher = publisher;
    }

    /**
     * Landscape are registered for observation here.
     * <p>
     * On processing success, {@link ProcessingFinishedEvent} is fired and read here to register the landscape.
     */
    @Override
    public void onApplicationEvent(ProcessingFinishedEvent event) {
        LandscapeDescription landscapeDescription = event.getInput();
        Landscape landscape = event.getLandscape();

        observerMap.put(landscape.getIdentifier(), landscapeObserverPoolFactory.getPoolFor(landscape, landscapeDescription));
        LOGGER.info("Registered landscape {} for observation.", landscapeDescription);
    }

    /**
     * Polls for changes in landscapes.
     */
    @Scheduled(fixedDelayString = "${nivio.pollingMilliseconds}", initialDelay = 5000)
    public void poll() {
        LOGGER.info("Polling {} landscapes for changes.", observerMap.size());
        observerMap.entrySet().parallelStream().forEach(e -> check(e.getValue()));
    }

    /**
     * @return the currently observed landscapes.
     */
    Set<String> getObservedLandscapes() {
        return observerMap.keySet();
    }

    private void check(LandscapeObserverPool observerPool) {
        ObservedChange change = observerPool.getChange();
        if (change.getErrors().size() > 0) {
            String errors = change.getErrors().stream().map(ProcessingException::getMessage).collect(Collectors.joining(";"));
            LOGGER.info("Errors occurred while scanning landscape {} for changes:  {}", observerPool.getLandscape(), errors);
        }

        Landscape stored = observerPool.getLandscape();
        LOGGER.debug("Detected {} changes in landscape {}", change.getChanges().size(), stored.getIdentifier());

        if (change.getChanges().size() > 0) {
            String s = StringUtils.collectionToDelimitedString(change.getChanges(), ";");
            LandscapeDescription updated = landscapeDescriptionFactory.from(stored);
            LOGGER.info("Detected change '{}' in landscape {}", s, stored.getIdentifier());
            if (updated != null) {
                publisher.publishEvent(new IndexEvent(updated, "Source change: " + s));
            }
        }

    }
}
