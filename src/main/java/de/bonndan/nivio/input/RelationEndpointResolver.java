package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.LandscapeItem;
import de.bonndan.nivio.model.RelationBuilder;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Optional;

/**
 * Resolves the dynamic endpoints of relations.
 */
public class RelationEndpointResolver {

    private final ProcessLog log;

    public RelationEndpointResolver(ProcessLog log) {
        this.log = log;
    }

    public void processRelations(LandscapeDescription landscape) {
        landscape.getItemDescriptions().all().forEach(itemDescription -> resolveRelations(itemDescription, landscape.getItemDescriptions()));
    }

    private void resolveRelations(ItemDescription description, ItemDescriptions allItems) {

        //providers
        description.getProvidedBy().forEach(term -> {
            allItems.query(term).stream().findFirst().ifPresentOrElse(o -> {
                RelationDescription rel = RelationBuilder.createProviderDescription((ItemDescription) o, description.getIdentifier());
                description.addRelation(rel);
            }, () -> log.warn(description.getIdentifier() + ": no provider target found for term " + term));
        });

        //other relations
        description.getRelations().forEach(rel -> {

            resolveOne(description, rel.getSource(), allItems).ifPresent(resolvedSource -> {
                ((RelationDescription) rel).setSource(resolvedSource.getFullyQualifiedIdentifier().toString());
            });

            resolveOne(description, rel.getTarget(), allItems).ifPresent(resolvedTarget -> {
                ((RelationDescription) rel).setTarget(resolvedTarget.getFullyQualifiedIdentifier().toString());
            });
        });

    }

    private Optional<ItemDescription> resolveOne(ItemDescription description, String term, ItemDescriptions allItems) {

        if (StringUtils.isEmpty(term)) {
            return Optional.of(description);
        }

        Collection<? extends LandscapeItem> result = allItems.query(term);
        if (result.size() > 1) {
            log.warn(description.getIdentifier() + ": Found ambiguous sources matching " + term);
            return Optional.empty();
        } else if (result.size() == 0) {
            log.warn(description.getIdentifier() + ": Found no sources matching " + term);
            return Optional.empty();
        }

        return Optional.of((ItemDescription)result.iterator().next());
    }
}
