package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.*;
import de.bonndan.nivio.model.IndexReadAccess;
import de.bonndan.nivio.model.RelationFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Resolves the dynamic endpoints of relations.
 */
public class RelationEndpointResolver implements Resolver {

    @NonNull
    @Override
    public LandscapeDescription resolve(@NonNull final LandscapeDescription input) {
        IndexReadAccess<ComponentDescription> indexReadAccess = input.getReadAccess();
        indexReadAccess.all(ItemDescription.class).forEach(itemDescription -> {
            try {
                resolveProvidedBy(itemDescription, input);
                resolveRelations(itemDescription, input);
            } catch (Exception e) {
                input.getProcessLog().error(
                        new ProcessingException(input, String.format("Failed to resolve relation for item description %s", itemDescription), e)
                );
            }
        });

        return LandscapeDescriptionFactory.refreshedCopyOf(input);
    }

    private void resolveProvidedBy(final ItemDescription description,
                                   final LandscapeDescription input
    ) {
        //providers
        description.getProvidedBy().forEach(term -> {
            var provider = input.getReadAccess()
                    .matchOrSearchByIdentifierOrName(term, ItemDescription.class)
                    .stream()
                    .findFirst();
            provider.ifPresentOrElse(
                    source -> {
                        try {
                            RelationDescription rel = RelationFactory.createProviderDescription(source, description.getIdentifier());
                            description.addOrReplaceRelation(rel);
                        } catch (IllegalArgumentException e) {
                            input.getProcessLog().error("Failed to create relation: " + e.getMessage());
                        }
                    },
                    () -> input.getProcessLog().warn(description.getIdentifier() + ": no provider target found for term " + term)
            );
        });

    }

    private void resolveRelations(final ItemDescription description,
                                  final LandscapeDescription input
    ) {
        description.getRelations().forEach(rel -> {

            var source = rel.getSource();
            var parentIdentifier = "";
            if (!StringUtils.hasLength(source) || rel.getSource().equalsIgnoreCase(description.getIdentifier())) {
                source = description.getIdentifier();
                parentIdentifier = description.getParentIdentifier();
            }

            try {
                Optional<ItemDescription> sourceDTO = input.getReadAccess().matchOneByIdentifiers(source, parentIdentifier, ItemDescription.class);
                sourceDTO.ifPresent(resolvedSource -> rel.setSource(resolvedSource.getFullyQualifiedIdentifier().toString()));

                var targets = input.getReadAccess().matchOrSearchByIdentifierOrName(rel.getTarget(), ItemDescription.class);
                Optional<ItemDescription> target = targets.stream().findFirst();
                target.ifPresent(resolvedTarget -> rel.setTarget(resolvedTarget.getFullyQualifiedIdentifier().toString()));
            } catch (IllegalArgumentException e) {
                input.getProcessLog().error("Failed to create relation: " + e.getMessage());
            }
        });

    }

}
