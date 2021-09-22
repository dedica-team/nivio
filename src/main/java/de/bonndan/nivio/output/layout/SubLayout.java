package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Layout for one group (for the items INSIDE the group).
 *
 */
public class SubLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubLayout.class);

    //higher means more space between items
    public static final int FORCE_CONSTANT = 150;

    //distance when repulsion has no more effect
    public static final int MAX_DISTANCE_LIMIT = 300;

    //affects iterations, think of cooling down
    private static final int INITIAL_TEMP = 300;

    private final FastOrganicLayout layout;
    private final Component parent;

    public SubLayout(Component group, Set<Item> items, LandscapeConfig.LayoutConfig itemLayoutConfig) {
        String name = group.getName();
        this.parent = group;

        List<LayoutedComponent> list = new ArrayList<>();
        items.forEach(item -> {
            List<Component> relationTargets = new ArrayList<>();
            item.getRelations().forEach(relationItem -> {
                if (!relationItem.getSource().equals(item))
                    return;

                Item other = relationItem.getTarget();
                if (item.getGroup().equals(other.getGroup())) {
                    relationTargets.add(other);
                }

            });
            LayoutedComponent e = new LayoutedComponent(item, relationTargets);
            e.setDefaultColor(group.getColor());
            list.add(e);
        });


        layout = new FastOrganicLayout(list);
        layout.setForceConstant(FORCE_CONSTANT);
        layout.setMaxDistanceLimit(MAX_DISTANCE_LIMIT);
        layout.setInitialTemp(INITIAL_TEMP);
        layout.configure(itemLayoutConfig);
        layout.execute();
        LOGGER.debug("Subgraph {} layouted items: {}", name, layout.getBounds());
    }

    public LayoutedComponent getOuterBounds() {
        return layout.getOuterBounds(parent);
    }
}
