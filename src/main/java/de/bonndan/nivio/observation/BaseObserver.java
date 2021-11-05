package de.bonndan.nivio.observation;

import de.bonndan.nivio.model.Landscape;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Base class for change observers. Provides access to landscape and event publisher, which are required to broadcast changes.
 */
public abstract class BaseObserver implements InputFormatObserver {

    protected final Landscape landscape;
    protected final ApplicationEventPublisher eventPublisher;

    protected BaseObserver(@NonNull final Landscape landscape, @NonNull final ApplicationEventPublisher eventPublisher) {
        this.landscape = Objects.requireNonNull(landscape);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }
}
