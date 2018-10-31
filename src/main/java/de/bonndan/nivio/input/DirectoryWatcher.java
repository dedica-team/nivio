package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

public class DirectoryWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

    private final ApplicationEventPublisher publisher;
    private final File file;

    public DirectoryWatcher(ApplicationEventPublisher publisher, File file) {
        this.publisher = publisher;
        this.file = file;
    }

    public void run() {
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.error("Could not create new watchservice", e);
            throw new ProcessingException("Could not create new watchservice", e);
        }

        Path path = file.isDirectory() ? Paths.get(file.getPath()) : Paths.get(file.getParent());

        try {
            path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            logger.error("Could not create new watchservice", e);
            throw new ProcessingException("Could not create new watchservice for " + path, e);
        }

        WatchKey key;
        logger.info("Starting directory watcher on " + path.toAbsolutePath());
        try {
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    publisher.publishEvent(new FSChangeEvent(this, event, path));
                    logger.info("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                }
                key.reset();
            }
        } catch (InterruptedException e) {
            logger.warn("Directory watcher was interrupted");
        }
    }

}
