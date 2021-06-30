package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.search.ItemIndex;
import de.bonndan.nivio.model.RelationBuilder;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Optional;

/**
 * Resolves the dynamic endpoints of relations.
 */
public class RelationEndpointResolver extends Resolver {

    protected RelationEndpointResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void resolve(LandscapeDescription landscape) {
        landscape.getItemDescriptions().all().forEach(itemDescription -> {
            try {
                resolveRelations(itemDescription, landscape.getItemDescriptions());
            } catch (Exception e) {
                processLog.error(
                        new ProcessingException(String.format("Failed to resolve relation for item description %s", itemDescription), e)
                );
            }
        });
    }

    private void resolveRelations(final ItemDescription description, ItemIndex<ItemDescription> allItems) {

        //providers
        description.getProvidedBy().forEach(term -> {
            allItems.query(term).stream().findFirst().ifPresentOrElse(o -> {
                        RelationDescription rel = RelationBuilder.createProviderDescription(o, description.getIdentifier());
                        description.addRelation(rel);
                    },
                    () -> processLog.warn(description.getIdentifier() + ": no provider target found for term " + term));
        });

        //other relations
        description.getRelations().forEach(rel -> {

            resolveOne(description, rel.getSource(), allItems)
                    .ifPresent(resolvedSource -> rel.setSource(resolvedSource.getFullyQualifiedIdentifier().toString()));

            resolveOne(description, rel.getTarget(), allItems)
                    .ifPresent(resolvedTarget -> rel.setTarget(resolvedTarget.getFullyQualifiedIdentifier().toString()));
        });

    }

    private Optional<ItemDescription> resolveOne(ItemDescription description, String term, ItemIndex<ItemDescription> allItems) {

        if (StringUtils.isEmpty(term)) {
            return Optional.of(description);
        }

        Collection<ItemDescription> result = allItems.query(term);
        if (result.size() > 1) {
            return allItems.firstWithGroup(result, description.getGroup());
        } else if (result.size() == 0) {
            processLog.warn(String.format("%s: Found no sources matching %s", description.getIdentifier(), term));
            return Optional.empty();
        }

        return Optional.of(result.iterator().next());
    }
}
