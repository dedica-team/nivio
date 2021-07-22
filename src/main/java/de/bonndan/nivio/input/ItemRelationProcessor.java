package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * Creates {@link Relation}s between {@link Item}s.
 */
public class ItemRelationProcessor extends Processor {

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
                                processLog.info(String.format(origin + ": Updating relation between %s and %s", update.getSource(), update.getTarget()));
                                changelog.addEntry(update, ProcessingChangelog.ChangeType.UPDATED, String.join(";", changes));
                            }
                            return update;
                        })
                        .orElseGet(() -> {
                            Relation created = RelationFactory.create(origin, relationDescription, landscape);
                            processLog.info(String.format(origin + ": Adding relation between %s and %s", created.getSource(), created.getTarget()));
                            changelog.addEntry(created, ProcessingChangelog.ChangeType.CREATED, null);
                            return created;
                        });
                assignToBothEnds(origin, current);
                processed.add(current);
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
                        processLog.info(String.format(origin + ": Removing relation between %s and %s", relation.getSource(), relation.getTarget()));
                        Item currentSource = landscape.getItems().pick(
                                relation.getSource().getFullyQualifiedIdentifier().getItem(),
                                relation.getSource().getFullyQualifiedIdentifier().getGroup()
                        );
                        if (!currentSource.removeRelation(relation)) {
                            processLog.warn(String.format("Could not remove relation %s from source %s", relation, relation.getSource()));
                        }

                        Item currentTarget = landscape.getItems().pick(
                                relation.getTarget().getFullyQualifiedIdentifier().getItem(),
                                relation.getTarget().getFullyQualifiedIdentifier().getGroup()
                        );
                        if (!currentTarget.removeRelation(relation)) {
                            processLog.warn(String.format("Could not remove relation %s from target %s", relation, relation.getSource()));
                        }
                        changelog.addEntry(relation, ProcessingChangelog.ChangeType.DELETED, null);
                    });
        });

        return changelog;
    }

    private boolean isValid(RelationDescription relationDescription, Landscape landscape) {

        List<Item> source = landscape.findBy(relationDescription.getSource());
        if (source.isEmpty()) {
            processLog.warn(String.format("Relation source %s not found", relationDescription.getSource()));
            return false;
        }

        List<Item> target = landscape.findBy(relationDescription.getTarget());
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
