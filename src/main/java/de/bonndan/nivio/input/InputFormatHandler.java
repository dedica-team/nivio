package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URL;
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
     * Add data (item descriptions, groups, kpis...) from a source reference to the landscape input data.
     *
     * @param reference            the input source
     * @param baseUrl              parent config url
     * @param landscapeDescription the input dto to modify
     */
    void applyData(SourceReference reference, @Nullable URL baseUrl, LandscapeDescription landscapeDescription);

    /**
     * Returns an observer for the source reference.
     *
     * @param inner an observer for files or urls
     * @param sourceReference the {@link SourceReference} to observe
     * @return observer that can handle the format or null if no observer is available
     */
    @Nullable
    InputFormatObserver getObserver(@NonNull final InputFormatObserver inner, @NonNull final SourceReference sourceReference);

}
