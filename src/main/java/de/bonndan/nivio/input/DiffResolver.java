package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Compares the input {@link LandscapeDescription} against the existing {@link LandscapeImpl}.
 *
 * Adds, updates and removes items in the landscape.
 */
public class DiffResolver extends Resolver {

    protected DiffResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, LandscapeImpl landscape) {
        Set<Item> existingItems = landscape.getItems().all();

        //insert new ones
        List<LandscapeItem> newItems = added(input.getItemDescriptions().all(), existingItems);
        Set<Item> inLandscape = new HashSet<>();
        processLog.info("Adding " + newItems.size() + " items in env " + landscape.getIdentifier());
        newItems.forEach(
                serviceDescription -> {
                    processLog.info("Creating new service " + serviceDescription.getIdentifier() + " in env " + input.getIdentifier());
                    Item created = ItemFactory.fromDescription(serviceDescription, landscape);
                    inLandscape.add(created);
                }
        );

        //update existing
        List<LandscapeItem> kept = new ArrayList<>();
        if (input.isPartial()) {
            kept.addAll(existingItems); //we want to keep all, increment does not contain all items
        } else {
            kept = kept(input.getItemDescriptions().all(), existingItems);
        }
        processLog.info("Updating " + kept.size() + " items in landscape " + landscape.getIdentifier());
        kept.forEach(
                item -> {

                    ItemDescription description = (ItemDescription) input.getItemDescriptions().find(ItemMatcher.forTarget(item)).orElse(null);
                    if (description == null) {
                        if (input.isPartial()) {
                            inLandscape.add((Item) item);
                            return;
                        } else {
                            throw new ProcessingException(input, "Item not found " + item.getIdentifier());
                        }
                    }

                    processLog.info("Updating item " + item.getIdentifier() + " in landscape " + input.getIdentifier());

                    ItemFactory.assignAll((Item) item, description);
                    inLandscape.add((Item) item);
                }
        );

        landscape.setItems(inLandscape);
        deleteUnreferenced(input, inLandscape, existingItems, processLog)
                .forEach(item -> landscape.getItems().all().remove(item));
    }

    private List<LandscapeItem> deleteUnreferenced(
            final LandscapeDescription landscapeDescription,
            Set<Item> kept,
            Set<Item> all,
            ProcessLog logger
    ) {
        if (landscapeDescription.isPartial()) {
            logger.info("Incremental change, will not remove any unreferenced items.");
            return new ArrayList<>();
        }

        List<LandscapeItem> removed = removed(kept, all);
        logger.info("Removing " + removed.size() + " sources in env " + landscapeDescription.getIdentifier());
        return removed;
    }

    /**
     * Returns all elements kept in the second list.
     */
    static List<LandscapeItem> kept(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> items2) {
        return items2.stream().filter(item -> exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements removed from the second list.
     */
    static List<LandscapeItem> removed(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> items2) {
        return items2.stream().filter(item -> !exists(item, items1)).collect(Collectors.toList());
    }

    /**
     * Returns all elements which are not in the second list
     */
    static List<LandscapeItem> added(Collection<? extends LandscapeItem> items1, Collection<? extends LandscapeItem> existing) {
        return items1.stream()
                .filter(item -> !exists(item, existing))
                .collect(Collectors.toList());
    }

    private static boolean exists(LandscapeItem item, Collection<? extends LandscapeItem> items) {
        return items.stream().anyMatch(
                inList -> ItemMatcher.forTarget(item).isSimilarTo(inList)
        );
    }
}
