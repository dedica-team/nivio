package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.IndexReadAccess;
import de.bonndan.nivio.model.RelationFactory;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Resolves the dynamic endpoints of relations.
 */
public class RelationEndpointResolver extends Resolver {

    protected RelationEndpointResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void resolve(LandscapeDescription input) {
        IndexReadAccess<ComponentDescription> indexReadAccess = input.getIndexReadAccess();
        indexReadAccess.all(ItemDescription.class).forEach(itemDescription -> {
            try {
                resolveProvidedBy(itemDescription, indexReadAccess);
                resolveRelations(itemDescription, indexReadAccess);
            } catch (Exception e) {
                processLog.error(
                        new ProcessingException(input, String.format("Failed to resolve relation for item description %s", itemDescription), e)
                );
            }
        });
    }

    private void resolveProvidedBy(final ItemDescription description,
                                   final IndexReadAccess<ComponentDescription> readAccess
    ) {
        //providers
        description.getProvidedBy().forEach(term -> {
            var provider = readAccess.matchOrSearchByIdentifierOrName(term, ItemDescription.class).stream().findFirst();
            provider.ifPresentOrElse(
                    source -> {
                        try {
                            RelationDescription rel = RelationFactory.createProviderDescription(source, description.getIdentifier());
                            description.addOrReplaceRelation(rel);
                        } catch (IllegalArgumentException e) {
                            processLog.error("Failed to create relation: " + e.getMessage());
                        }
                    },
                    () -> processLog.warn(description.getIdentifier() + ": no provider target found for term " + term)
            );
        });

    }

    private void resolveRelations(final ItemDescription description,
                                  final IndexReadAccess<ComponentDescription> readAccess
    ) {
        description.getRelations().forEach(rel -> {

            var source = rel.getSource();
            var parentIdentifier = "";
            if (!StringUtils.hasLength(source) || rel.getSource().equalsIgnoreCase(description.getIdentifier())) {
                source = description.getIdentifier();
                parentIdentifier = description.getParentIdentifier();
            }

            try {
                Optional<ItemDescription> sourceDTO = readAccess.matchOneByIdentifiers(source, parentIdentifier, ItemDescription.class);
                sourceDTO.ifPresent(resolvedSource -> rel.setSource(resolvedSource.getFullyQualifiedIdentifier().toString()));

                Optional<ItemDescription> target = readAccess.matchOrSearchByIdentifierOrName(rel.getTarget(), ItemDescription.class).stream().findFirst();
                target.ifPresent(resolvedTarget -> rel.setTarget(resolvedTarget.getFullyQualifiedIdentifier().toString()));
            } catch (IllegalArgumentException e) {
                processLog.error("Failed to create relation: " + e.getMessage());
            }
        });

    }

}
