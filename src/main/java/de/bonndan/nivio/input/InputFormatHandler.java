package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.SourceReference;
import de.bonndan.nivio.observation.InputFormatObserver;
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
     * Returns item descriptions generated from the source.
     *
     * @param reference the input source
     * @param baseUrl   parent config url
     * @return list of generated items (needs to be merged with existing items)
     */
    List<ItemDescription> getDescriptions(SourceReference reference, @Nullable URL baseUrl);

    /**
     * Returns an observer for the source reference.
     *
     * @param reference the source reference with a format
     * @param baseUrl   the url of the landscape description
     * @return observer that can handle the format or null if no observer is available
     */
    @Nullable
    InputFormatObserver getObserver(SourceReference reference, @Nullable URL baseUrl);

}
