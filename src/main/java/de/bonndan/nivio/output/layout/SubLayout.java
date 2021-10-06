package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Layout for one group (for the items INSIDE the group).
 */
public class SubLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubLayout.class);

    //higher means more space between items
    public static final int FORCE_CONSTANT = 150;

    //distance when repulsion has no more effect
    public static final int MAX_DISTANCE_LIMIT = 250;

    //affects iterations, think of cooling down
    public static final int INITIAL_TEMP = 300;
    public static final double MIN_DISTANCE_LIMIT = 50;

    private FastOrganicLayout layout;
    private Component parent;
    private final boolean debug;

    public SubLayout(boolean debug) {
        this.debug = debug;
    }

    public void render(Component group, Set<Item> items, LandscapeConfig.LayoutConfig itemLayoutConfig) {
        String name = group.getName();
        this.parent = group;

        List<LayoutedComponent> components = getComponents(group, items);
        layout = new FastOrganicLayout(components, MIN_DISTANCE_LIMIT, MAX_DISTANCE_LIMIT, INITIAL_TEMP, itemLayoutConfig);
        layout.setDebug(debug);
        layout.execute();
        LOGGER.debug("Subgraph {} layouted items: {}", name, layout.getNodes());
    }

    static List<LayoutedComponent> getComponents(final Component group, final Set<Item> items) {
        List<LayoutedComponent> components = new ArrayList<>();
        List<Relation> added = new ArrayList<>();
        items.forEach(item -> {
            List<Component> relationTargets = new ArrayList<>();
            item.getRelations().forEach(relation1 -> {
                if (!relation1.getSource().equals(item))
                    return;

                Item other = relation1.getTarget();
                if (!item.getGroup().equals(other.getGroup())) {
                    return;
                }

                //remove opposite relations, otherwise we encounter double attraction resulting in too close placement
                Optional<Relation> reversed = other.getRelations().stream().filter(relation -> relation.getTarget().equals(item)).findFirst();
                if (reversed.isPresent() && added.contains(reversed.get())) {
                    return;
                }
                if (!added.contains(relation1)) {
                    added.add(relation1);
                    relationTargets.add(other);
                }

            });
            LayoutedComponent e = new LayoutedComponent(item, relationTargets);
            e.setDefaultColor(group.getColor());
            components.add(e);
        });
        return components;
    }

    public LayoutedComponent getOuterBounds() {
        return layout.getOuterBounds(parent);
    }
}
