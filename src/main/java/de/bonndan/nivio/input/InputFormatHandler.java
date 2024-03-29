package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.observation.InputFormatObserver;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Processors of input sources must implement this interface.
 */
public interface InputFormatHandler {

    /**
     * Returns the supported format.
     *
     * @return string representing input formats (nivio, k8s, docker...)
     */
    List<String> getFormats();

    /**
     * Read DTOs (item descriptions, groups, kpis...) from a source reference to the landscape input data.
     *
     * @param reference        the input source
     * @param defaultLandscape the default DTO to apply components to if no other landscape are created bis this method
     * @return all created landscapes. The default one does not need to be returned.
     */
    List<LandscapeDescription> applyData(@NonNull final SourceReference reference, @NonNull final LandscapeDescription defaultLandscape);

    /**
     * Returns an observer for the source reference.
     *
     * @param inner           a default observer for files or urls
     * @param eventPublisher  publisher to use when {@link de.bonndan.nivio.observation.InputChangedEvent} are fired
     * @param sourceReference the {@link SourceReference} to observe
     * @return observer that can handle the format or null if no observer is available
     */
    @Nullable
    default InputFormatObserver getObserver(@NonNull final InputFormatObserver inner,
                                            @NonNull final ApplicationEventPublisher eventPublisher,
                                            @NonNull final SourceReference sourceReference
    ) {
        return null;
    }

}
