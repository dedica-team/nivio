package de.bonndan.nivio.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
    private static final long GRACE_PERIOD = 2000;

    public LocalFileObserver(@NonNull final ApplicationEventPublisher eventPublisher,
                             @NonNull final File file
    ) {
        super(eventPublisher);
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
        LOGGER.info("Watching path {} for changes.", path);

        try {
            path.register(watchService, ENTRY_MODIFY);
            boolean poll = true;
            while (poll) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    final Path changed = (Path) event.context();
                    File file = path.toFile();
                    long lastModified = file.lastModified();
                    boolean isWatchedFile = changed.endsWith(this.file.getName());
                    boolean triggersEvent = (file.length() > 0) && (lastModified - this.lastModified) > GRACE_PERIOD;
                    if (isWatchedFile && triggersEvent) {
                        this.lastModified = lastModified;
                        triggerChange(file);
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

    private void triggerChange(File file) {
        try {
            eventPublisher.publishEvent(new InputChangedEvent(file.toURI().toURL(), new ObservedChange(file.toString())));
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to convert file {} to URL", file);
        }
    }
}
