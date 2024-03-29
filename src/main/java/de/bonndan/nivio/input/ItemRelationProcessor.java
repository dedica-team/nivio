package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ItemIndex;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * Creates {@link Relation}s between {@link Item}s.
 */
public class ItemRelationProcessor extends Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemRelationProcessor.class);

    protected ItemRelationProcessor(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public ProcessingChangelog process(@NonNull final LandscapeDescription input, @NonNull final Landscape landscape) {

        ProcessingChangelog changelog = new ProcessingChangelog();

        // process configured relations of all input dtos
        List<Relation> processed = new ArrayList<>();
        input.getItemDescriptions().all().forEach(itemDescription -> {
            final Item origin = landscape.getItems().pick(itemDescription);

            for (RelationDescription relationDescription : itemDescription.getRelations()) {

                if (!isValid(relationDescription, landscape)) {
                    continue;
                }

                Relation current = getCurrentRelation(relationDescription, landscape, origin)
                        .map(relation -> {
                            Relation update = RelationFactory.update(relation, relationDescription, landscape);
                            List<String> changes = relation.getChanges(update);
                            if (!changes.isEmpty()) {
                                processLog.info(String.format("%s: Updating relation between %s and %s", origin, update.getSource(), update.getTarget()));
                                changelog.addEntry(update, ProcessingChangelog.ChangeType.UPDATED, changes);
                            }
                            return update;
                        })
                        .orElseGet(() -> {
                            try {
                                Relation created = RelationFactory.create(origin, relationDescription, landscape);
                                processLog.info(String.format("%s: Adding relation between %s and %s", origin, created.getSource(), created.getTarget()));
                                changelog.addEntry(created, ProcessingChangelog.ChangeType.CREATED, null);
                                return created;
                            } catch (IllegalArgumentException e) {
                                processLog.warn(String.format("%s: Failed to create relation: %s", origin, e.getMessage()));
                                return null;
                            }
                        });
                if (current != null) {
                    assignToBothEnds(origin, current);
                    processed.add(current);
                }
            }
        });

        if (input.isPartial()) {
            return changelog;
        }

        // delete what has not been configured and is left over in landscape
        input.getItemDescriptions().all().forEach(itemDescription -> {
            final Item origin = landscape.getItems().pick(itemDescription);
            Collection<Relation> toDelete = CollectionUtils.subtract(origin.getRelations(), processed);
            toDelete.stream()
                    .filter(relation -> !processed.contains(relation))
                    .filter(relation -> origin.equals(relation.getSource()))
                    .forEach(relation -> {
                        processLog.info(String.format("%s: Removing relation between %s and %s", origin, relation.getSource(), relation.getTarget()));
                        removeRelationFromItem(landscape.getItems(), relation, relation.getSource());
                        removeRelationFromItem(landscape.getItems(), relation, relation.getTarget());
                        changelog.addEntry(relation, ProcessingChangelog.ChangeType.DELETED, null);
                    });
        });

        return changelog;
    }

    /**
     * Gracefully finds the relation end item in the landscape and tries to remove the relation.
     *
     * @param itemIndex   all landscape items (containing a sibling of the relation source or target)
     * @param relation    the relation to remove
     * @param relationEnd the relation source or target
     */
    private void removeRelationFromItem(ItemIndex<Item> itemIndex, Relation relation, Item relationEnd) {
        var fqi = relationEnd.getFullyQualifiedIdentifier();
        try {
            Item sibling = itemIndex.pick(fqi.getItem(), fqi.getGroup());
            boolean isRelationRemoved = sibling.removeRelation(relation);
            if (!isRelationRemoved) {
                processLog.warn(String.format("Could not remove relation %s from item %s", relation, relationEnd));
            }
        } catch (NoSuchElementException e) {
            String msg = String.format("Could not find relation end %s from relation %s: %s", relationEnd, relation, e.getMessage());
            processLog.error(msg);
            LOGGER.error(msg, e);
        }
    }

    private boolean isValid(RelationDescription relationDescription, Landscape landscape) {

        List<Item> source = landscape.getItems().findBy(relationDescription.getSource());
        if (source.isEmpty()) {
            processLog.warn(String.format("Relation source %s not found", relationDescription.getSource()));
            return false;
        }

        List<Item> target = landscape.getItems().findBy(relationDescription.getTarget());
        if (target.isEmpty()) {
            processLog.warn(String.format("Relation target %s not found", relationDescription.getTarget()));
            return false;
        }

        return true;
    }

    private void assignToBothEnds(Item origin, Relation relation) {
        origin.addOrReplace(relation);
        if (relation.getSource() == origin) {
            relation.getTarget().addOrReplace(relation);
        } else {
            relation.getSource().addOrReplace(relation);
        }
    }

    private Optional<Relation> getCurrentRelation(RelationDescription relationDescription,
                                                  Landscape landscape,
                                                  Item origin
    ) {
        Item source = landscape.findOneBy(relationDescription.getSource(), origin.getGroup());
        Item target = landscape.findOneBy(relationDescription.getTarget(), origin.getGroup());

        if (source.equals(target)) {
            return Optional.empty();
        }

        Iterator<Relation> iterator = origin.getRelations().iterator();
        Relation virtual = RelationFactory.createForTesting(source, target);
        Relation existing;
        while (iterator.hasNext()) {
            existing = iterator.next();
            if (existing.equals(virtual)) {
                return Optional.of(existing);
            }
        }

        return Optional.empty();
    }

}
