package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.util.URLHelper;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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

    public void resolve(final LandscapeDescription landscapeDescription, Map<ItemDescription, List<String>> templatesAndTargets) {

        URL baseUrl = URLHelper.getParentPath(landscapeDescription.getSource()).orElse(null);
        landscapeDescription.getSourceReferences().forEach(ref -> {
            try {
                InputFormatHandler factory = formatFactory.getInputFormatHandler(ref, landscapeDescription);
                landscapeDescription.addItems(factory.getDescriptions(ref, baseUrl));
                ref.getAssignTemplates().forEach((key, identifiers) -> templatesAndTargets.put(landscapeDescription.getTemplates().get(key), identifiers));
            } catch (ProcessingException ex) {
                log.warn("Failed to resolve source reference '" + ref.getUrl() + "' (" + ref.getFormat() + ")", ex);
                landscapeDescription.setIsPartial(true);
            }
        });
    }

}
