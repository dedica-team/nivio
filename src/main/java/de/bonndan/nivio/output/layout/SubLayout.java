package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Component;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LayoutConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Layout for one group (for the items INSIDE the group).
 *
 */
public class SubLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubLayout.class);

    private FastOrganicLayout layout;
    private Component parent;
    private final boolean debug;
    private final LayoutConfig layoutConfig;

    public SubLayout(boolean debug, @NonNull final LayoutConfig layoutConfig) {
        this.debug = debug;
        this.layoutConfig = layoutConfig;
    }

    public void render(@NonNull final Component group, @NonNull final Set<Item> items) {
        this.parent = Objects.requireNonNull(group);
        String name = group.getName();

        List<LayoutedComponent> components = getLayoutedComponents(group, items);

        layout = new FastOrganicLayout(
                components,
                new CollisionRegardingForces(layoutConfig.getItemMinDistanceLimit(), layoutConfig.getItemMaxDistanceLimit()),
                layoutConfig.getItemLayoutInitialTemp()
        );
        layout.setDebug(debug);
        layout.execute();
        LOGGER.debug("Subgraph {} layouted items: {}", name, layout.getNodes());
    }

    static List<LayoutedComponent> getLayoutedComponents(Component group, Set<Item> items) {
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
        return list;
    }

    public LayoutedComponent getOuterBounds() {
        return LayoutedComponent.from(parent, layout.nodes);
    }
}
