package de.bonndan.nivio.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * A wrapper around observers to reduce the async results to a single boolean.
 */
public class ObserverPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverPool.class);

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<InputFormatObserver, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();
    private final long delay;

    public ObserverPool(@NonNull final ThreadPoolTaskScheduler taskScheduler, long delay) {
        this.taskScheduler = Objects.requireNonNull(taskScheduler);
        this.delay = delay;
    }

    /**
     * Replace the current observers with new ones.
     *
     * Current observers are stopped
     *
     * @param observers new observers
     */
    public void updateObservers(List<InputFormatObserver> observers) {

        LOGGER.info("Received {} observers", observers.size());
        for (Map.Entry<InputFormatObserver, ScheduledFuture<?>> futureEntry : scheduledTasks.entrySet()) {
            futureEntry.getValue().cancel(true);
        }
        scheduledTasks.clear();

        observers.forEach(inputFormatObserver -> {
            try {
                ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleWithFixedDelay(inputFormatObserver, delay);
                scheduledTasks.put(inputFormatObserver, scheduledFuture);
            } catch (TaskRejectedException e) {
                LOGGER.error("Failed to schedule observer: " + e.getMessage(), e);
            }
        });
    }
}
