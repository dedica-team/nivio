package de.bonndan.nivio.input;

import de.bonndan.nivio.ProcessingException;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Compares the input {@link LandscapeDescription} against the existing {@link Landscape}.
 *
 * Adds, updates and removes items in the landscape.
 */
public class DiffProcessor extends Processor {

    protected DiffProcessor(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void process(LandscapeDescription input, Landscape landscape) {
        Set<Item> existingItems = landscape.getItems().all();

        //insert new ones
        List<Item> newItems = added(input.getItemDescriptions().all(), existingItems, landscape);
        Set<Item> inLandscape = new HashSet<>();
        processLog.info("Adding " + newItems.size() + " items in env " + landscape.getIdentifier());
        newItems.forEach(
                newItem -> {
                    processLog.info("Creating new service " + newItem.getIdentifier() + " in env " + input.getIdentifier());
                    inLandscape.add(newItem);
                }
        );

        //update existing
        List<Item> kept;
        if (input.isPartial()) {
            kept = new ArrayList<>(existingItems); //we want to keep all, increment does not contain all items
        } else {
            kept = kept(input.getItemDescriptions().all(), existingItems, landscape);
        }
        processLog.info("Updating " + kept.size() + " items in landscape " + landscape.getIdentifier());
        kept.forEach(
                item -> {

                    ItemDescription description = input.getItemDescriptions().find(ItemMatcher.forTarget(item)).orElse(null);
                    if (description == null) {
                        if (input.isPartial()) {
                            inLandscape.add(item);
                            return;
                        } else {
                            throw new ProcessingException(input, "Item not found " + item.getIdentifier());
                        }
                    }

                    processLog.info("Updating item " + item.getIdentifier() + " in landscape " + input.getIdentifier());

                    ItemFactory.assignAll(item, description);
                    inLandscape.add(item);
                }
        );

        landscape.setItems(inLandscape);
        deleteUnreferenced(input, inLandscape, existingItems, processLog)
                .forEach(item -> landscape.getItems().all().remove(item));
    }

    private List<Item> deleteUnreferenced(
            final LandscapeDescription landscapeDescription,
            Set<Item> kept,
            Set<Item> all,
            ProcessLog logger
    ) {
        if (landscapeDescription.isPartial()) {
            logger.info("Incremental change, will not remove any unreferenced items.");
            return new ArrayList<>();
        }

        List<Item> removed = removed(kept, all);
        logger.info("Removing " + removed.size() + " sources in env " + landscapeDescription.getIdentifier());
        return removed;
    }

    /**
     * Returns all items that are also present in the new itemDescriptions
     */
    static List<Item> kept(Collection<? extends ItemDescription> newItems, Collection<? extends Item> items, Landscape landscape) {
        return items.stream()
                .filter(item -> presentInNewItems(item, newItems, landscape))
                .collect(Collectors.toList());
    }

    /**
     * Returns all elements removed from the second list.
     */
    static List<Item> removed(Collection<? extends Item> items, Collection<? extends Item> itemDescriptions) {
        return itemDescriptions.stream()
                .filter(item -> doesNotExistAsItem(item, items))
                .collect(Collectors.toList());
    }

    /**
     * Returns all elements which are not in the second list
     */
    static List<Item> added(Collection<? extends ItemDescription> itemDescriptions, Collection<? extends Item> existingItems, Landscape landscape) {
        return itemDescriptions.stream()
                .map(itemDescription -> ItemFactory.fromDescription(itemDescription, landscape))
                .filter(newItem -> doesNotExistAsItem(newItem, existingItems))
                .collect(Collectors.toList());
    }

    private static boolean presentInNewItems(Item item, Collection<? extends ItemDescription> newItems, Landscape landscape) {
        return newItems.stream()
                .map(newItem -> ItemFactory.fromDescription(newItem, landscape))
                .anyMatch(inList -> ItemMatcher.forTarget(item).isSimilarTo(inList.getFullyQualifiedIdentifier()));
    }

    private static boolean doesNotExistAsItem(Item item, Collection<? extends Item> items) {
        return items.stream().noneMatch(
                inList -> ItemMatcher.forTarget(item).isSimilarTo(inList.getFullyQualifiedIdentifier())
        );
    }
}
