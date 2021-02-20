package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.util.URLHelper;

import java.net.URL;


/**
 * Resolves source references into collections of item descriptions.
 *
 *
 */
public class SourceReferencesResolver {

    private final InputFormatHandlerFactory formatFactory;
    private final ProcessLog log;

    public SourceReferencesResolver(InputFormatHandlerFactory formatFactory, ProcessLog logger) {
        this.formatFactory = formatFactory;
        this.log = logger;
    }

    public void resolve(final LandscapeDescription landscapeDescription) {

        URL baseUrl = URLHelper.getParentPath(landscapeDescription.getSource()).orElse(null);
        landscapeDescription.getSourceReferences().forEach(ref -> {
            try {
                formatFactory.getInputFormatHandler(ref).applyData(ref, baseUrl, landscapeDescription);
            } catch (ProcessingException ex) {
                log.warn(String.format("Failed to resolve source reference '%s' (%s)", ref.getUrl(), ref.getFormat()), ex);
                landscapeDescription.setIsPartial(true);
            }
        });
    }

}
