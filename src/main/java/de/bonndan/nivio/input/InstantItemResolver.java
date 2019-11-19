package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.*;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * If the landscape is configured as "greedy", new items are created on the fly by a reference name.
 *
 * @todo add hosts as soon as relation resolver supports hosts
 */
public class InstantItemResolver {

    private final ProcessLog log;

    public InstantItemResolver(ProcessLog log) {
        this.log = log;
    }

    public void processTargets(LandscapeDescription landscape) {
        List<ItemDescription> all = landscape.getItemDescriptions();

        if (!landscape.getConfig().isGreedy())
            return;

        HashSet<ItemDescription> newItems = new HashSet<>();
        all.forEach(itemDescription -> newItems.addAll(resolveTargets(itemDescription, all)));

        landscape.addItems(newItems);
    }

    private List<ItemDescription> resolveTargets(ItemDescription description, List<ItemDescription> allItems) {

        List<ItemDescription> newItems = new ArrayList<>();
        //providers
        description.getProvidedBy().forEach(term -> {
            Optional<? extends LandscapeItem> provider = Items.query(term.toLowerCase(), allItems).stream().findFirst();

            if (provider.isEmpty()) {
                newItems.add(createItem(term));
            }
        });

        //other relations
        description.getRelations().forEach(rel -> {

            //skip sources, since only the targets are in the list

            //find targets
            if (!StringUtils.isEmpty(rel.getTarget()) && !hasTarget(rel.getTarget().toLowerCase(), allItems)) {
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
        FullyQualifiedIdentifier fqi = FullyQualifiedIdentifier.from(term);
        itemDescription.setGroup(fqi.getGroup());
        itemDescription.setIdentifier(fqi.getIdentifier());
        return itemDescription;
    }

    private boolean hasTarget(String term, List<ItemDescription> allItems) {

        if (StringUtils.isEmpty(term)) {
            return true;
        }

        Collection<? extends LandscapeItem> result = Items.query(term, allItems);
        if (result.size() > 1) {
            log.warn("Found ambiguous sources matching " + term);
            return true;
        }

        return result.size() != 0;
    }
}
