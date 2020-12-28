package de.bonndan.nivio.observation;

import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.InputFormatHandler;
import de.bonndan.nivio.input.InputFormatHandlerFactory;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * This factory is responsible to create {@link LandscapeObserverPool}s.
 * <p>
 * Since each landscape can consist of different sources ({@link SourceReference}s) of different formats, each of them
 * can require a different type of observer ({@link InputFormatObserver}).
 */
@Service
public class LandscapeObserverPoolFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandscapeObserverPoolFactory.class);
    private final InputFormatHandlerFactory inputFormatHandlerFactory;
    private final FileFetcher fileFetcher;

    public LandscapeObserverPoolFactory(InputFormatHandlerFactory inputFormatHandlerFactory, FileFetcher fileFetcher) {
        this.inputFormatHandlerFactory = inputFormatHandlerFactory;
        this.fileFetcher = fileFetcher;
    }

    public LandscapeObserverPool getPoolFor(Landscape landscape, @NonNull LandscapeDescription description) {

        List<InputFormatObserver> observers = new ArrayList<>();
        Optional<URL> baseUrl = URLHelper.getParentPath(description.getSource());
        if (baseUrl.isEmpty()) {
            LOGGER.info("Landscape {} does not seem to have a valid source ('" + description.getSource() + "')", description.getIdentifier());
        } else {
            URLHelper.getURL(description.getSource()).ifPresent(url -> observers.add(new URLObserver(fileFetcher, url)));
        }

        for (SourceReference sourceReference : description.getSourceReferences()) {
            InputFormatHandler inputFormatHandler = inputFormatHandlerFactory.getInputFormatHandler(sourceReference);
            InputFormatObserver observer = inputFormatHandler.getObserver(sourceReference, baseUrl.orElse(null));
            if (observer != null) {
                observers.add(observer);
            }
        }

        return new LandscapeObserverPool(landscape, observers);
    }

}
