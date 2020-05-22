package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingErrorEvent;
import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Deprecated
@Component
public class WatcherFactory {

    private static final Logger logger = LoggerFactory.getLogger(WatcherFactory.class);

    private final ApplicationEventPublisher publisher;

    private final FileFetcher fileFetcher;
    private final Indexer indexer;

    public WatcherFactory(ApplicationEventPublisher publisher, FileFetcher fileFetcher, Indexer indexer) {
        this.publisher = publisher;
        this.fileFetcher = fileFetcher;
        this.indexer = indexer;
    }

    public List<Runnable> getWatchers(List<URL> locations) {

        List<Runnable> runnables = new ArrayList<>();

        logger.info("getWatchers {}", locations);

        locations.forEach(url -> {
            LandscapeDescription env = null;
            if (URLHelper.isLocal(url)) {
                DirectoryWatcher directoryWatcher;
                File file;
                try {
                    file = Paths.get(url.toURI()).toFile();
                    directoryWatcher = new DirectoryWatcher(publisher, file);
                } catch (URISyntaxException e) {
                    throw new ProcessingException("Failed to initialize watchers from seed", e);
                }
                runnables.add(directoryWatcher);
                logger.info("Created directory watcher for url " + url);
                try {
                    env = LandscapeDescriptionFactory.fromYaml(file);
                } catch (ReadingException ex) {
                    publisher.publishEvent(new ProcessingErrorEvent(this, ex));
                    logger.error("Failed to parse file {}", file);
                }
            } else {
                try {
                    env = LandscapeDescriptionFactory.fromString(fileFetcher.get(url), url);
                    Objects.requireNonNull(env);
                } catch (ReadingException ex) {
                    publisher.publishEvent(new ProcessingErrorEvent(this, ex));
                    logger.error("Failed to parse file {}", url);
                }
            }
            if (env != null) {
                indexer.reIndex(env);
            }
        });


        return runnables;
    }

    public Runnable getWatcher(File file) {
        return new DirectoryWatcher(publisher, file);
    }
}
