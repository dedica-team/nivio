package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.model.ServiceItems;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static de.bonndan.nivio.model.ServiceItems.find;

/**
 * Resolves source references into collections of item descriptions.
 *
 *
 */
public class SourceReferencesResolver {

    private final ProcessLog log;

    public SourceReferencesResolver(ProcessLog logger) {
        this.log = logger;
    }

    public void resolve(final LandscapeDescription landscapeDescription, Map<ItemDescription, List<String>> templatesAndTargets) {

        landscapeDescription.getSourceReferences().forEach(ref -> {
            try {
                ItemDescriptionFactory factory = ItemDescriptionFormatFactory.getFactory(ref, landscapeDescription);
                landscapeDescription.addItems(factory.getDescriptions(ref));
                ref.getAssignTemplates().forEach((key, identifiers) -> templatesAndTargets.put(landscapeDescription.getTemplates().get(key), identifiers));
            } catch (ProcessingException ex) {
                log.warn("Failed to resolve source reference " + ref, ex);
                landscapeDescription.setIsPartial(true);
            }
        });

    }

}
