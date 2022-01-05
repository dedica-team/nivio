package de.bonndan.nivio.observation;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Base class for change observers. Provides access to landscape and event publisher, which are required to broadcast changes.
 */
public abstract class BaseObserver implements InputFormatObserver {

    protected final ApplicationEventPublisher eventPublisher;

    protected BaseObserver(@NonNull final ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }
}
