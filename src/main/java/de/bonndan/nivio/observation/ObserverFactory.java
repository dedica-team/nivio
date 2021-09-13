package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.InputFormatHandlerFactory;
import de.bonndan.nivio.input.SeedConfiguration;
import de.bonndan.nivio.input.SourceReference;
import de.bonndan.nivio.util.URLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * This factory is responsible to create {@link InputFormatObserver}s.
 *
 * Since each {@link SeedConfiguration} can consist of different sources ({@link SourceReference}s) of different formats, each of them
 * can require a different type of observer ({@link InputFormatObserver}).
 */
@Service
public class ObserverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverFactory.class);

    private final InputFormatHandlerFactory inputFormatHandlerFactory;
    private final FileFetcher fileFetcher;
    private final ApplicationEventPublisher eventPublisher;

    public ObserverFactory(@NonNull final InputFormatHandlerFactory inputFormatHandlerFactory,
                           @NonNull final FileFetcher fileFetcher,
                           @NonNull final ApplicationEventPublisher eventPublisher
    ) {
        this.inputFormatHandlerFactory = inputFormatHandlerFactory;
        this.fileFetcher = fileFetcher;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates observers for each {@link SourceReference} of a landscape and the landscape url itself.
     *
     * @param description new landscape input data
     * @return new observers
     */
    public List<InputFormatObserver> getObserversFor(@NonNull final SeedConfiguration description) {

        List<InputFormatObserver> observers = new ArrayList<>();
        Optional<URL> url = description.getSource() != null ? description.getSource().getURL() : Optional.empty();
        Optional<URL> baseUrl = Optional.empty();
        if (url.isPresent()) {
            baseUrl = URLFactory.getParentPath(url.get());
            if (baseUrl.isEmpty()) {
                LOGGER.info("Cannot create observer for landscape '{}' source '{}' ", description.getIdentifier(), url.get());
            } else {
                observers.add(getObserver(url.get()));
            }
        }

        for (SourceReference sourceReference : description.getSourceReferences()) {
            InputFormatHandler inputFormatHandler = inputFormatHandlerFactory.getInputFormatHandler(sourceReference);
            InputFormatObserver observer;
            try {
                observer = getObserver(new URL(URLFactory.combine(baseUrl.orElse(null), sourceReference.getUrl().toString())));
            } catch (MalformedURLException e) {
                LOGGER.warn("Failed to create observer for base url {} and source reference {}", baseUrl.orElse(null), sourceReference.getUrl());
                continue;
            }

            observer = inputFormatHandler.getObserver(observer, sourceReference);
            if (observer != null) {
                observers.add(observer);
            }
        }

        return observers;
    }

    private InputFormatObserver getObserver(URL url) {
        if (URLFactory.isLocal(url)) {
            try {
                return new LocalFileObserver(eventPublisher, new File(url.toURI()));
            } catch (URISyntaxException e) {
                LOGGER.error("Could not create a local file observer for {}", url);
            }
        }
        return new RemoteURLObserver(eventPublisher, fileFetcher, url);
    }
}
