package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.IndexReadAccess;
import de.bonndan.nivio.model.RelationFactory;
import org.springframework.util.StringUtils;

/**
 * Resolves the dynamic endpoints of relations.
 */
public class RelationEndpointResolver extends Resolver {

    protected RelationEndpointResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void resolve(LandscapeDescription landscape) {
        landscape.getItemDescriptions().forEach(itemDescription -> {
            try {
                resolveRelations(itemDescription, landscape.getIndexReadAccess());
            } catch (Exception e) {
                processLog.error(
                        new ProcessingException(landscape, String.format("Failed to resolve relation for item description %s", itemDescription), e)
                );
            }
        });
    }

    private void resolveRelations(final ItemDescription description, IndexReadAccess<ComponentDescription> readAccess) {

        //providers
        description.getProvidedBy().forEach(term -> {
            readAccess.matchOrSearchByIdentifierOrName(term, ItemDescription.class).stream().findFirst().ifPresentOrElse(o -> {
                        RelationDescription rel = RelationFactory.createProviderDescription(o, description.getIdentifier());
                        description.addOrReplaceRelation(rel);
                    },
                    () -> processLog.warn(description.getIdentifier() + ": no provider target found for term " + term));
        });

        //other relations
        description.getRelations().forEach(rel -> {

            var source = rel.getSource();
            var parentIdentifier = "";
            if (!StringUtils.hasLength(source)) {
                source = description.getIdentifier();
                parentIdentifier = description.getParentIdentifier();
            }
            readAccess.matchOneByIdentifiers(source, parentIdentifier, ItemDescription.class)
                    .ifPresent(resolvedSource -> rel.setSource(resolvedSource.getFullyQualifiedIdentifier().toString()));

            readAccess.matchOneByIdentifiers(rel.getTarget(), null, ItemDescription.class)
                    .ifPresent(resolvedTarget -> rel.setTarget(resolvedTarget.getFullyQualifiedIdentifier().toString()));
        });

    }

}
