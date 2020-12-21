package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * If the landscape is configured as "greedy", new items are created on the fly by a reference name.
 */
public class InstantItemResolver extends Resolver {

    protected InstantItemResolver(ProcessLog processLog) {
        super(processLog);
    }

    @Override
    public void resolve(LandscapeDescription landscape) {

        if (!landscape.getConfig().isGreedy()) {
            return;
        }

        HashSet<ItemDescription> newItems = new HashSet<>();
        landscape.getItemDescriptions().all().forEach(itemDescription -> newItems.addAll(resolveTargets(itemDescription, landscape.getItemDescriptions())));

        landscape.addItems(newItems);
    }

    private List<ItemDescription> resolveTargets(ItemDescription description, ItemDescriptions allItems) {

        List<ItemDescription> newItems = new ArrayList<>();
        //providers
        description.getProvidedBy().forEach(term -> {
            Optional<? extends ItemDescription> provider = allItems.query(term.toLowerCase()).stream().findFirst();

            if (provider.isEmpty()) {
                processLog.info("Creating a new provider landscape item for term '" + term + "' instantly.");
                newItems.add(createItem(term));
            }
        });

        //other relations
        description.getRelations().forEach(rel -> {
            //inverse links, e.g. from docker compose
            String target = rel.getTarget().equalsIgnoreCase(description.getIdentifier()) ?
                    rel.getSource() : rel.getTarget();
            if (!StringUtils.isEmpty(target) && !hasTarget(target.toLowerCase(), allItems)) {
                processLog.info(description + ": creating a new target item '" + target.toLowerCase() + "' instantly.");
                newItems.add(createItem(rel.getTarget()));
            }
        });

        return newItems;
    }

    /**
     * Creates a new item description with source/target as identifier
     */
    private ItemDescription createItem(String term) {
        ItemDescription itemDescription = new ItemDescription();
        ItemMatcher fqi = ItemMatcher.forTarget(term);
        itemDescription.setGroup(fqi.getGroup());
        itemDescription.setIdentifier(fqi.getItem());

        return itemDescription;
    }

    private boolean hasTarget(String term, ItemDescriptions allItems) {

        Collection<? extends ItemDescription> result = allItems.query(term);
        if (result.size() > 1) {
            processLog.warn("Found ambiguous sources matching " + term);
            return true;
        }

        return result.size() != 0;
    }
}
