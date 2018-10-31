package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.util.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class WatcherFactory {

    private static final Logger logger = LoggerFactory.getLogger(WatcherFactory.class);

    private final Seed seed;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public WatcherFactory(Seed seed, ApplicationEventPublisher publisher) {
        this.seed = seed;
        this.publisher = publisher;
    }

    public List<Runnable> getWatchers() {

        List<Runnable> runnables = new ArrayList<>();
        try {
            seed.getLocations().forEach(url -> {
                if (URLHelper.isLocal(url)) {
                    DirectoryWatcher directoryWatcher = new DirectoryWatcher(publisher, new File(url.toString()));
                    runnables.add(directoryWatcher);
                    logger.info("Created directory watcher for url " + url);
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
