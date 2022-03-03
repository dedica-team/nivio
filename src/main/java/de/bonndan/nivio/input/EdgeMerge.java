package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ComponentMatcher;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Processes relations between {@link PhysicalComponent}s.
 *
 * TODO support {@link Part}
 */
public class EdgeMerge {

    private final IndexReadAccess<GraphComponent> indexReadAccess;
    private final GraphWriteAccess<GraphComponent> graphWriteAccess;

    public EdgeMerge(IndexReadAccess<GraphComponent> indexReadAccess,
                     GraphWriteAccess<GraphComponent> graphWriteAccess
    ) {

        this.indexReadAccess = indexReadAccess;
        this.graphWriteAccess = graphWriteAccess;
    }

    public ProcessingChangelog mergeAndDiff(List<ItemDescription> descriptions, ProcessLog processLog) {
        ProcessingChangelog changelog = new ProcessingChangelog();
        List<Relation> processed = new ArrayList<>();
        for (ComponentDescription item : descriptions) {
            item.getRelations().forEach(relationDescription -> processed.add(createOrUpdate(relationDescription, processLog, changelog)));
        }

        // delete what has not been configured and is left over in landscape
        descriptions.forEach(itemDescription -> {
            final Item origin;
            try {
                origin = indexReadAccess.matchOne(ComponentMatcher.forComponent(itemDescription.getFullyQualifiedIdentifier()), Item.class).orElseThrow();
            } catch (NoSuchElementException e) {
                processLog.warn(String.format("Could not delete relations from %s", itemDescription));
                return;
            }
            Collection<Relation> toDelete = CollectionUtils.subtract(origin.getRelations(), processed);
            toDelete.stream()
                    .filter(relation -> !processed.contains(relation))
                    .filter(relation -> origin.equals(relation.getSource()))
                    .forEach(relation -> {
                        graphWriteAccess.removeRelation(relation);
                        changelog.addEntry(relation, ProcessingChangelog.ChangeType.DELETED, List.of(String.format("%s: Removing relation between %s and %s", origin, relation.getSource(), relation.getTarget())));
                    });
        });

        return changelog;
    }

    private Relation createOrUpdate(RelationDescription relationDescription, ProcessLog processLog, ProcessingChangelog changelog) {
        final Item origin;
        final Item target;
        try {
            ComponentMatcher sourceMatcher = ComponentMatcher.forComponent(relationDescription.getSource(), Item.class);
            origin = indexReadAccess.matchOne(sourceMatcher, Item.class)
                    .orElseThrow(() -> new NoSuchElementException(String.format("Not found anything for source %s", sourceMatcher)));

            ComponentMatcher targetMatcher = ComponentMatcher.forComponent(relationDescription.getTarget(), Item.class);
            target = indexReadAccess.matchOne(targetMatcher, Item.class)
                    .orElseThrow(() -> new NoSuchElementException(String.format("Not found anything for target %s", targetMatcher)));
        } catch (NoSuchElementException e) {
            processLog.warn(String.format("%s: Failed to create relation: %s", relationDescription, e.getMessage()));
            return null;
        }


        Optional<Relation> optionalRelation = indexReadAccess.findRelation(origin.getFullyQualifiedIdentifier(), target.getFullyQualifiedIdentifier());

        final String updateMessage = String.format("Updated relation between %s and %s", origin, target);
        Relation current = optionalRelation.map(relation -> {
                    Relation update = RelationFactory.update(relation, relationDescription, origin, target);
                    List<String> changes = relation.getChanges(update);
                    if (!changes.isEmpty()) {
                        processLog.info(updateMessage);
                        changelog.addEntry(update, ProcessingChangelog.ChangeType.UPDATED, changes);
                    }
                    return update;
                })
                .orElseGet(() -> {
                    try {
                        Relation created = RelationFactory.create(origin, target, relationDescription);
                        processLog.info(String.format("Adding relation between %s and %s", origin, target));
                        changelog.addEntry(created, ProcessingChangelog.ChangeType.CREATED, null);
                        return created;
                    } catch (IllegalArgumentException e) {
                        processLog.warn(String.format("%s: Failed to create relation: %s", origin, e.getMessage()));
                        return null;
                    }
                });
        if (current != null) {
            graphWriteAccess.addOrReplaceRelation(current);
            changelog.addEntry(origin, ProcessingChangelog.ChangeType.UPDATED, List.of(updateMessage));
            changelog.addEntry(target, ProcessingChangelog.ChangeType.UPDATED, List.of(updateMessage));
        }
        return current;
    }
}
