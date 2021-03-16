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

        input.getItemDescriptions().all().forEach(itemDescription -> {
            Item origin = landscape.getItems().pick(itemDescription);
            List<Relation> affected = new ArrayList<>();

            for (RelationDescription relationDescription : itemDescription.getRelations()) {

                if (!isValid(relationDescription, landscape)) {
                    continue;
                }

                Optional<Relation> update = update(relationDescription, landscape, origin);
                if (update.isPresent()) {
                    Relation relation = update.get();
                    affected.add(relation);
                    processLog.info(String.format("Updating relation between %s and %s", relation.getSource(), relation.getTarget()));
                    changelog.addEntry(relation, ProcessingChangelog.ChangeType.UPDATED);
                    continue;
                }

                Relation created = create(relationDescription, landscape);
                affected.add(created);
                processLog.info(String.format("Adding relation between %s and %s", created.getSource(), created.getTarget()));
                changelog.addEntry(created, ProcessingChangelog.ChangeType.CREATED);
            }

            affected.forEach(relation -> assignToBothEnds(origin, relation));
            Collection<Relation> toDelete = CollectionUtils.disjunction(origin.getRelations(), affected);
            toDelete.forEach(relation -> {
                if (!origin.equals(relation.getSource()))
                    return;
                removefromBothEnds(origin, relation);
                processLog.info(String.format("Removing relation between %s and %s", relation.getSource(), relation.getTarget()));
                changelog.addEntry(relation, ProcessingChangelog.ChangeType.DELETED);
            });
        });

        return changelog;
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
        removefromBothEnds(origin,relation);

        origin.getRelations().add(relation);
        if (relation.getSource() == origin) {
            relation.getTarget().getRelations().add(relation);
        } else {
            relation.getSource().getRelations().add(relation);
        }
    }
    private void removefromBothEnds(Item origin, Relation relation) {
        origin.getRelations().remove(relation);
        if (relation.getSource() == origin) {
            relation.getTarget().getRelations().remove(relation);
        } else {
            relation.getSource().getRelations().remove(relation);
        }
    }


    private Optional<Relation> update(RelationDescription relationDescription,
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
                return Optional.of(RelationBuilder.update(existing, relationDescription));
            }
        }

        return Optional.empty();
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
