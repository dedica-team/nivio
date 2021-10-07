package de.bonndan.nivio.observation;

/**
 * Observer for input sources.
 *
 * Will emit an {@link InputChangedEvent} if its observed source changes.
 *
 * URL observer is implemented, but others like k8s observer to be done
 */
public interface InputFormatObserver extends Runnable {
}
