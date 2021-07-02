package de.bonndan.nivio.observation;

import de.bonndan.nivio.model.Landscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Observer for local files.
 */
public class LocalFileObserver extends BaseObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileObserver.class);

    private final File file;
    private long lastModified = 0;

    /**
     * grace period (2s) to ignore multiple (duplicate) events
     */
    private static final long gracePeriod = 2000;

    public LocalFileObserver(@NonNull final Landscape landscape,
                             @NonNull final ApplicationEventPublisher eventPublisher,
                             @NonNull final File file
    ) {
        super(landscape, eventPublisher);
        if (!file.isFile()) {
            throw new IllegalArgumentException(String.format("Given arg must be a file: %s.", file));
        }
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("Given file must be local and exist: %s.", file));
        }
        this.file = Objects.requireNonNull(file);
    }

    @Override
    public void run() {
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            LOGGER.error("Failed to start watch service", e);
            return;
        }

        Path path = file.getParentFile().toPath();
        LOGGER.info(String.format("Watching path %s for changes.", path));

        try {
            path.register(watchService, ENTRY_MODIFY);
            boolean poll = true;
            while (poll) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    final Path changed = (Path) event.context();
                    long lastModified = path.toFile().lastModified();
                    boolean isWatchedFile = changed.endsWith(file.getName());
                    boolean triggersEvent = (path.toFile().length() > 0) && (lastModified - this.lastModified) > gracePeriod;
                    if (isWatchedFile && triggersEvent) {
                        this.lastModified = lastModified;
                        triggerChange();
                    } else {
                        LOGGER.debug("Ignoring event");
                    }
                }
                poll = key.reset();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to poll using watch service", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.debug("Interrupted");
        } finally {
            try {
                if (watchService != null) {
                    LOGGER.debug("Closing watchservice for {}", path);
                    watchService.close();
                }
            } catch (IOException e) {
                LOGGER.error("Failed to stop watch service", e);
            }
        }
    }

    private void triggerChange() {
        eventPublisher.publishEvent(new InputChangedEvent(new ObservedChange(landscape, file.toString())));
    }
}
