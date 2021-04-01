package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.search.ItemMatcher;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Creates {@link Relation}s between {@link Item}s.
 */
public class ItemRelationProcessor extends Processor {

    protected ItemRelationProcessor(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public ProcessingChangelog process(LandscapeDescription input, Landscape landscape) {

        ProcessingChangelog changelog = new ProcessingChangelog();
        List<Relation> processed = new ArrayList<>();

        input.getItemDescriptions().all().forEach(itemDescription -> {
            Item origin = landscape.getItems().pick(itemDescription);
            List<Relation> affected = new ArrayList<>();

            for (RelationDescription relationDescription : itemDescription.getRelations()) {

                if (!isValid(relationDescription, landscape)) {
                    continue;
                }

                Optional<Relation> current = getCurrentRelation(relationDescription, landscape, origin);
                current.ifPresentOrElse(
                        (relation) -> updateRelation(changelog, processed, affected, relationDescription, relation),
                        () -> createRelation(landscape, changelog, processed, origin, affected, relationDescription));
            }

            affected.forEach(relation -> assignToBothEnds(origin, relation));
            Collection<Relation> toDelete = CollectionUtils.subtract(origin.getRelations(), affected);
            toDelete.stream()
                    .filter(relation -> !processed.contains(relation))
                    .filter(relation -> origin.equals(relation.getSource()))
                    .filter(relation -> !input.isPartial())
                    .forEach(relation -> {
                        processLog.info(String.format("Removing relation between %s and %s", relation.getSource(), relation.getTarget()));
                        if (!relation.getSource().getRelations().remove(relation)) {
                            processLog.warn(String.format("Could not remove relation %s from source %s", relation, relation.getSource()));
                        }
                        if (!relation.getTarget().getRelations().remove(relation)) {
                            processLog.warn(String.format("Could not remove relation %s from target %s", relation, relation.getTarget()));
                        }
                        changelog.addEntry(relation, ProcessingChangelog.ChangeType.DELETED, null);
                    });
        });

        return changelog;
    }

    private void updateRelation(ProcessingChangelog changelog, List<Relation> processed, List<Relation> affected, RelationDescription relationDescription, Relation relation) {
        Relation update = update(relationDescription, relation);
        affected.add(update);
        processed.add(update);
        processLog.info(String.format("Updating relation between %s and %s", update.getSource(), update.getTarget()));
        List<String> changes = relation.getChanges(update);
        if (!changes.isEmpty()) {
            changelog.addEntry(update, ProcessingChangelog.ChangeType.UPDATED, String.join(";", changes));
        }
    }

    private void createRelation(Landscape landscape, ProcessingChangelog changelog, List<Relation> processed, Item origin, List<Relation> affected, RelationDescription relationDescription) {
        Relation created = create(relationDescription, landscape);
        affected.add(created);
        processed.add(created);
        processLog.info(String.format(origin + ": Adding relation between %s and %s", created.getSource(), created.getTarget()));
        changelog.addEntry(created, ProcessingChangelog.ChangeType.CREATED, null);
    }

    private boolean isValid(RelationDescription relationDescription, Landscape landscape) {

        Optional<Item> source = findBy(relationDescription.getSource(), landscape);
        if (source.isEmpty()) {
            processLog.warn(String.format("Relation source %s not found", relationDescription.getSource()));
            return false;
        }

        Optional<Item> target = findBy(relationDescription.getTarget(), landscape);
        if (target.isEmpty()) {
            processLog.warn(String.format("Relation target %s not found", relationDescription.getTarget()));
            return false;
        }

        return true;
    }

    private void assignToBothEnds(Item origin, Relation relation) {
        origin.getRelations().add(relation);
        if (relation.getSource() == origin) {
            relation.getTarget().getRelations().add(relation);
        } else {
            relation.getSource().getRelations().add(relation);
        }
    }

    private Optional<Relation> getCurrentRelation(RelationDescription relationDescription,
                                                  Landscape landscape,
                                                  Item origin
    ) {
        Item source = findBy(relationDescription.getSource(), landscape).orElseThrow();
        Item target = findBy(relationDescription.getTarget(), landscape).orElseThrow();

        Iterator<Relation> iterator = origin.getRelations().iterator();
        Relation created = new Relation(source, target);
        Relation existing;
        while (iterator.hasNext()) {
            existing = iterator.next();
            if (existing.equals(created)) {
                return Optional.of(existing);
            }
        }

        return Optional.empty();
    }

    private Relation update(RelationDescription relationDescription, Relation existing
    ) {
        return RelationBuilder.update(existing, relationDescription);
    }

    private Relation create(RelationDescription relationDescription, Landscape landscape) {

        return new Relation(
                findBy(relationDescription.getSource(), landscape).orElseThrow(),
                findBy(relationDescription.getTarget(), landscape).orElseThrow(),
                relationDescription.getDescription(),
                relationDescription.getFormat(),
                relationDescription.getType()
        );
    }

    private Optional<Item> findBy(String term, Landscape landscape) {
        return ItemMatcher.forTarget(term)
                .map(itemMatcher -> landscape.getItems().find(itemMatcher))
                .orElseGet(() -> landscape.getItems().query(term).stream().findFirst());
    }
}
