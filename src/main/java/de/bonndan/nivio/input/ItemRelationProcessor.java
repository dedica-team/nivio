package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationFactory;
import de.bonndan.nivio.search.ComponentMatcher;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * Creates {@link Relation}s between {@link Item}s.
 */
@Deprecated
public class ItemRelationProcessor extends Processor {

    protected ItemRelationProcessor(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public ProcessingChangelog process(@NonNull final LandscapeDescription input, @NonNull final Landscape landscape) {

        ProcessingChangelog changelog = new ProcessingChangelog();

        // process configured relations of all input dtos
        List<Relation> processed = new ArrayList<>();
        input.getItemDescriptions().forEach(itemDescription -> {
            final Item origin = landscape.getIndexReadAccess()
                    .findOneMatching(ComponentMatcher.forTarget(itemDescription.getFullyQualifiedIdentifier()), Item.class).orElseThrow();

            for (RelationDescription relationDescription : itemDescription.getRelations()) {

                Relation current = getCurrentRelation(relationDescription, landscape, origin)
                        .map(relation -> {
                            Relation update = RelationFactory.update(relation, relationDescription, origin, (Item) relation.getTarget());
                            List<String> changes = relation.getChanges(update);
                            if (!changes.isEmpty()) {
                                processLog.info(String.format("%s: Updating relation between %s and %s", origin, update.getSource(), update.getTarget()));
                                changelog.addEntry(update, ProcessingChangelog.ChangeType.UPDATED, changes);
                            }
                            return update;
                        })
                        .orElseGet(() -> {
                            try {
                                Optional<Item> target = landscape.getIndexReadAccess().findOneByIdentifiers(relationDescription.getTarget(), null, Item.class);
                                Relation created = RelationFactory.create(origin, target.orElseThrow(), relationDescription);
                                processLog.info(String.format("%s: Adding relation between %s and %s", origin, created.getSource(), created.getTarget()));
                                changelog.addEntry(created, ProcessingChangelog.ChangeType.CREATED, null);
                                return created;
                            } catch (IllegalArgumentException e) {
                                processLog.warn(String.format("%s: Failed to create relation: %s", origin, e.getMessage()));
                                return null;
                            }
                        });
                if (current != null) {
                    landscape.getIndexWriteAccess().addOrReplaceRelation(current);
                    processed.add(current);
                }
            }
        });

        if (input.isPartial()) {
            return changelog;
        }

        // delete what has not been configured and is left over in landscape
        input.getItemDescriptions().forEach(itemDescription -> {
            final Item origin = landscape.getIndexReadAccess()
                    .findOneMatching(ComponentMatcher.forTarget(itemDescription.getFullyQualifiedIdentifier()), Item.class).orElseThrow();
            Collection<Relation> toDelete = CollectionUtils.subtract(origin.getRelations(), processed);
            toDelete.stream()
                    .filter(relation -> !processed.contains(relation))
                    .filter(relation -> origin.equals(relation.getSource()))
                    .forEach(relation -> {
                        processLog.info(String.format("%s: Removing relation between %s and %s", origin, relation.getSource(), relation.getTarget()));
                        landscape.getIndexWriteAccess().removeRelation(relation);
                        changelog.addEntry(relation, ProcessingChangelog.ChangeType.DELETED, null);
                    });
        });

        return changelog;
    }

    private Optional<Relation> getCurrentRelation(RelationDescription relationDescription,
                                                  Landscape landscape,
                                                  Item origin
    ) {
        ComponentMatcher sourceMatcher = ComponentMatcher.build(null, null, null, relationDescription.getSource(), origin.getParent().getIdentifier());
        Item source = landscape.getIndexReadAccess().findOneMatching(sourceMatcher, Item.class).orElseThrow();
        ComponentMatcher targetMatcher = ComponentMatcher.build(null, null, null, relationDescription.getTarget(), origin.getParent().getIdentifier());
        Item target = landscape.getIndexReadAccess().findOneMatching(targetMatcher, Item.class).orElseThrow();

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
