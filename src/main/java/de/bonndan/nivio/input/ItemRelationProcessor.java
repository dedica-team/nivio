package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ItemMatcher;

import java.util.Iterator;
import java.util.Optional;

/**
 * Creates {@link Relation}s between {@link Item}s.
 */
public class ItemRelationProcessor extends Processor {

    protected ItemRelationProcessor(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, Landscape landscape) {
        input.getItemDescriptions().all().forEach(serviceDescription -> {
            Item origin = landscape.getItems().pick(serviceDescription);
            if (!input.isPartial()) {
                processLog.debug(String.format("Clearing relations of %s", origin));
                origin.getRelations().clear(); //delete all relations on full update
            }
        });

        input.getItemDescriptions().all().forEach(itemDescription -> {
            Item origin = landscape.getItems().pick(itemDescription);

            for (RelationDescription relationDescription : itemDescription.getRelations()) {
                Optional<Relation> update = update(relationDescription, landscape, origin);
                update.ifPresent(relation -> {
                    origin.getRelations().remove(relation);
                    origin.getRelations().add(relation);
                    if (relation.getSource() == origin) {
                        relation.getTarget().getRelations().add(relation);
                    } else {
                        relation.getSource().getRelations().add(relation);
                    }
                });
            }
        });
    }

    private Optional<Relation> update(RelationDescription relationDescription, Landscape landscape, Item origin) {

        var fqiSource = ItemMatcher.forTarget(relationDescription.getSource());
        var fqiTarget = ItemMatcher.forTarget(relationDescription.getTarget());
        Item source = landscape.getItems().find(fqiSource).orElse(null);
        if (source == null) {
            processLog.warn(String.format("Relation source %s not found", relationDescription.getSource()));
            return Optional.empty();
        }
        Item target = landscape.getItems().find(fqiTarget).orElse(null);

        if (target == null) {
            processLog.warn(String.format("Relation target %s not found", relationDescription.getTarget()));
            return Optional.empty();
        }

        Iterator<Relation> iterator = origin.getRelations().iterator();
        Relation existing = null;
        Relation created = new Relation(source, target);
        while (iterator.hasNext()) {
            existing = iterator.next();
            if (existing.equals(created)) {
                processLog.info(String.format("Updating relation between %s and %s", existing.getSource(), existing.getTarget()));
                return Optional.of(RelationBuilder.update(existing, relationDescription));
            }
        }

        created = new Relation(created.getSource(),
                created.getTarget(),
                relationDescription.getDescription(),
                relationDescription.getFormat(),
                relationDescription.getType()
        );

        processLog.info(String.format("Adding relation from %s to %s", created.getSource(), created.getTarget()));
        return Optional.of(created);
    }
}
