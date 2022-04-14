package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.GraphComponent;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sorts items within a group into chains, i.e. starting at the item which has relations within the group and following
 * the relations.
 */
public class ItemSorter {

    private ItemSorter() {}

    public static List<Item> sort(@Nullable final Collection<Item> groupItems) {

        if (groupItems == null || groupItems.isEmpty()) {
            return new ArrayList<>();
        }

        var in = new ArrayList<>(groupItems);
        List<Item> out = new ArrayList<>();
        Set<URI> continued = new HashSet<>();
        Map<URI, Item> uriItemMap = groupItems.stream().collect(Collectors.toMap(GraphComponent::getFullyQualifiedIdentifier, item -> item));

        Item item = in.get(0);
        while (!in.isEmpty()) {
            final var currentItem = item;
            Optional<Item> hasSourceInGroup = item.getRelations().stream()
                    .filter(relation -> !relation.getSource().equals(currentItem) && uriItemMap.containsKey(relation.getSource().getFullyQualifiedIdentifier()))
                    .map(Relation::getSource)
                    .findFirst();

            //continue with the source
            if (hasSourceInGroup.isPresent() && !continued.contains(item.getFullyQualifiedIdentifier())) {
                item = hasSourceInGroup.get();
                continued.add(item.getFullyQualifiedIdentifier());
                continue;
            }

            //remove current
            out.add(item);
            in.remove(item);
            uriItemMap.remove(item.getFullyQualifiedIdentifier());

            //continue with target
            item = item.getRelations().stream()
                    .filter(relation -> !relation.getTarget().equals(currentItem) && uriItemMap.containsKey(relation.getTarget().getFullyQualifiedIdentifier()))
                    .map(Relation::getTarget)
                    .findFirst().orElseGet(() -> in.isEmpty() ?  null : in.iterator().next());
        }

        return out;
    }
}
