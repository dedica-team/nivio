package de.bonndan.nivio.observation;

import de.bonndan.nivio.IndexEvent;
import de.bonndan.nivio.ProcessingFinishedEvent;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service to register landscapes to observe description source changes.
 */
@Service
public class ObserverRegistry implements ApplicationListener<ProcessingFinishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverRegistry.class);

    private final Map<String, LandscapeObserverPool> observerMap = new ConcurrentHashMap<>();
    private final URLObserverFactory urlObserverFactory;
    private final LandscapeDescriptionFactory landscapeDescriptionFactory;
    private final ApplicationEventPublisher publisher;

    public ObserverRegistry(URLObserverFactory urlObserverFactory,
                            LandscapeDescriptionFactory landscapeDescriptionFactory,
                            ApplicationEventPublisher publisher
    ) {
        this.urlObserverFactory = urlObserverFactory;
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
        LandscapeDescription from = (LandscapeDescription) event.getSource();
        LandscapeImpl landscape = (LandscapeImpl) event.getLandscape();

        if (from == null) {
            String msg = "No landscape description (input) available. Landscape " + landscape.getIdentifier() + "could not be registered for observation";
            landscape.getLog().warn(msg);
            LOGGER.warn(msg);
            return;
        }

        URL sourceUrl = URLHelper.getURL(from.getSource());
        if (sourceUrl == null) {
            LOGGER.info("Landscape {} does not seem to have a valid source ('" + from.getSource() + "')", from.getIdentifier());
        }

        List<URL> landscapeSourceLocations = getLandscapeSourceLocations(from, sourceUrl);
        setLandscapeUrls(from, landscapeSourceLocations);
        LOGGER.info("Registered landscape {} for observation with {} urls.", from, landscapeSourceLocations.size());
    }

    /**
     * Returns all URLs of a landscape description.
     *
     * @param env description
     * @param url config file url
     * @return urls: config file and source references.
     */
    private List<URL> getLandscapeSourceLocations(@NonNull LandscapeDescription env, @Nullable URL url) {
        List<URL> urls = new ArrayList<>();
        if (url != null) {
            urls.add(url);
        }

        URL baseUrl = URLHelper.getParentPath(env.getSource());
        for (SourceReference sourceReference : env.getSourceReferences()) {
            try {
                urls.add(new URL(URLHelper.combine(baseUrl, sourceReference.getUrl())));
            } catch (MalformedURLException e) {
                LOGGER.error("Failed to handle url {}", sourceReference.getUrl(), e);
            }
        }

        return urls;
    }

    /**
     * Polls for changes in landscapes.
     */
    @Scheduled(fixedDelay = 20000, initialDelay = 5000)
    public void poll() {
        LOGGER.info("Polling {} landscapes for changes.", observerMap.size());
        observerMap.entrySet().parallelStream().forEach(e -> check(e.getValue()));
    }

    /**
     * @return the currently observed landscapes.
     */
    public Set<String> getObservedLandscapes() {
        return observerMap.keySet();
    }

    private void setLandscapeUrls(Landscape landscape, List<URL> urls) {
        observerMap.put(
                landscape.getIdentifier(),
                new LandscapeObserverPool(
                        landscape,
                        urls.stream().map(urlObserverFactory::getObserver).collect(Collectors.toList())
                )
        );
    }

    private void check(LandscapeObserverPool observerPool) {
        Optional<String> change = observerPool.hasChange();
        change.ifPresent(s -> {
            Landscape stored = observerPool.getLandscape();
            LandscapeDescription updated = landscapeDescriptionFactory.from(stored);
            LOGGER.info("Detected change '{}' in landscape {}", s, stored.getIdentifier());
            if (updated != null) {
                publisher.publishEvent(new IndexEvent(this, updated, "Source change: " + s));
            }
        });
    }
}
