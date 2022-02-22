package de.bonndan.nivio.input;

import de.bonndan.nivio.input.dto.ComponentDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.IndexReadAccess;
import de.bonndan.nivio.search.ComponentMatcher;
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
        landscape.getIndexReadAccess().all(ItemDescription.class).forEach(itemDescription -> newItems.addAll(
                resolveTargets(itemDescription, landscape.getIndexReadAccess()))
        );

        landscape.mergeItems(newItems);
    }

    private List<ItemDescription> resolveTargets(ItemDescription description, IndexReadAccess<ComponentDescription> readAccess) {

        List<ItemDescription> newItems = new ArrayList<>();
        //providers
        description.getProvidedBy().forEach(term -> {
            Optional<ItemDescription> provider = readAccess.matchOrSearchByIdentifierOrName(term.toLowerCase(), ItemDescription.class).stream().findFirst();

            if (provider.isEmpty()) {
                processLog.info("Creating a new provider landscape item for term '" + term + "' instantly.");
                newItems.add(createItem(term));
            }
        });

        //other relations
        description.getRelations().forEach(rel -> {
            //inverse links, e.g. from docker compose
            if (rel.getTarget() == null) {
                processLog.warn("Found relation " + rel + " without target");
                return;
            }
            String target = rel.getTarget().equalsIgnoreCase(description.getIdentifier()) ?
                    rel.getSource() : rel.getTarget();
            if (StringUtils.hasLength(target) && !hasTarget(target.toLowerCase(), readAccess)) {
                processLog.info(String.format("%s: creating a new target item '%s' instantly.", description, target.toLowerCase()));
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

        ComponentMatcher componentMatcher = ComponentMatcher.forTarget(term);
        itemDescription.setGroup(componentMatcher.getGroup());
        itemDescription.setIdentifier(componentMatcher.getItem());

        return itemDescription;
    }

    private boolean hasTarget(String term, IndexReadAccess<ComponentDescription> allItems) {

        Collection<ItemDescription> result = allItems.match(ComponentMatcher.forTarget(term), ItemDescription.class);
        if (result.size() > 1) {
            processLog.warn("Found ambiguous sources matching " + term);
            return true;
        }

        return !result.isEmpty();
    }
}
