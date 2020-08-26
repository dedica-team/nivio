package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Layout for one group (for the items INSIDE the group).
 * <p>
 */
public class SubLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubLayout.class);
    public static final int FORCE_CONSTANT = 50;
    public static final int MAX_DISTANCE_LIMIT = 200;

    private final FastOrganicLayout layout;
    private final Component parent;

    public SubLayout(Component parent, Set<Item> items, LandscapeConfig.LayoutConfig itemLayoutConfig) {
        String name = parent.getName();
        this.parent = parent;

        List<LayoutedComponent> list = new ArrayList<>();
        items.forEach(item -> {
            List<Component> relationTargets = new ArrayList<>();
            item.getRelations().forEach(relationItem -> {
                if (!relationItem.getSource().equals(item))
                    return;

                Item other = relationItem.getTarget();
                if (item.getGroup() == null)
                    throw new RuntimeException("Item " + item + "has no group");
                if (other.getGroup() == null)
                    throw new RuntimeException("Item " + other + "has no group");


                if (item.getGroup().equals(other.getGroup())) {
                    relationTargets.add(other);
                }

            });
            list.add(new LayoutedComponent(item, relationTargets));
        });


        layout = new FastOrganicLayout(list);
        layout.setForceConstant(FORCE_CONSTANT);
        layout.setMaxDistanceLimit(MAX_DISTANCE_LIMIT);
        layout.configure(itemLayoutConfig);
        layout.execute();
        LOGGER.debug("Subgraph {} layouted items: {}", name, layout.getBounds());
    }

    public LayoutedComponent getOuterBounds() {
        return layout.getOuterBounds(parent);
    }
}
