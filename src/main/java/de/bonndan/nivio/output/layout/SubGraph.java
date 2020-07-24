package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Graph for one group (for the items INSIDE the group).
 * <p>
 */
public class SubGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubGraph.class);

    private final FastOrganicLayout layout;
    private final Component parent;

    public SubGraph(Component parent, List<Item> items) {
        String name = parent.getName();
        this.parent = parent;

        List<ComponentBounds> list = new ArrayList<>();
        items.forEach(item -> {
            List<Component> relationTargets = new ArrayList<>();
            item.getRelations().forEach(relationItem -> {
                if (!relationItem.getSource().equals(item))
                    return;

                Item other = relationItem.getTarget();
                boolean sameGroup = (item.getGroup() == null && other.getGroup() == null)
                        || item.getGroup().equals(other.getGroup());
                if (sameGroup)
                    relationTargets.add(other);

            });
            list.add(new ComponentBounds(item, relationTargets));
        });


        layout = new FastOrganicLayout(list);
        layout.setForceConstant(50);
        layout.setMaxDistanceLimit(200);
        layout.execute();
        LOGGER.debug("Subgraph {} layouted items: {}", name, layout.getBounds());
    }

    public ComponentBounds getOuterBounds() {
        return layout.getOuterBounds(parent);
    }
}
