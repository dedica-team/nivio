package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class WatcherFactory {

    private static final Logger logger = LoggerFactory.getLogger(WatcherFactory.class);

    private final Seed seed;

    private final ApplicationEventPublisher publisher;

    private final FileFetcher fileFetcher;
    private final Indexer indexer;

    @Autowired
    public WatcherFactory(Seed seed, ApplicationEventPublisher publisher, FileFetcher fileFetcher, Indexer indexer) {
        this.seed = seed;
        this.publisher = publisher;
        this.fileFetcher = fileFetcher;
        this.indexer = indexer;
    }

    public List<Runnable> getWatchers() {

        List<Runnable> runnables = new ArrayList<>();
        try {
            seed.getLocations().forEach(url -> {
                LandscapeDescription env = null;
                if (URLHelper.isLocal(url)) {
                    DirectoryWatcher directoryWatcher;
                    File file;
                    try {
                        file = new File(url.toURI());
                        directoryWatcher = new DirectoryWatcher(publisher, file);
                    } catch (URISyntaxException e) {
                        throw new ProcessingException("Failed to initialize watchers from seed", e);
                    }
                    runnables.add(directoryWatcher);
                    logger.info("Created directory watcher for url " + url);
                    try {
                        env = LandscapeDescriptionFactory.fromYaml(file);
                    } catch (ReadingException ex) {
                        logger.error("Failed to parse file");
                    }
                } else {
                    try {
                        env = LandscapeDescriptionFactory.fromString(fileFetcher.get(url), url);
                        Objects.requireNonNull(env);
                    } catch (ReadingException ex) {
                        logger.error("Failed to parse file");
                    }
                }
                if (env != null) {
                    indexer.reIndex(env);
                }
            });
        } catch (MalformedURLException e) {
            throw new ProcessingException("Failed to initialize watchers from seed", e);
        }

        return runnables;
    }

    public Runnable getWatcher(File file) {
        return new DirectoryWatcher(publisher, file);
    }
}
