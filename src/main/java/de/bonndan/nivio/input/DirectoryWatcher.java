package de.bonndan.nivio.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

@Component
public class DirectoryWatcher implements Runnable {

    public static final String NIVIO_ENV_DIRECTORY = "/opt/nivio/environments";

    private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

    private final ApplicationEventPublisher publisher;

    @Autowired
    public DirectoryWatcher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void run() {
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.error("Could not create new watchservice", e);
            throw new RuntimeException("Could not create new watchservice", e);
        }

        Path path = Paths.get(NIVIO_ENV_DIRECTORY);

        try {
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            logger.error("Could not create new watchservice", e);
            throw new RuntimeException("Could not create new watchservice", e);
        }

        WatchKey key;
        logger.info("Starting directory watcher");
        try {
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    publisher.publishEvent(new FSChangeEvent(this, event));
                    logger.info("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            logger.warn("Directory watcher was interrupted");
        }


    }

}
