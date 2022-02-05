package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LayoutConfig;
import de.bonndan.nivio.util.RootPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Renders a graph of group containers only, not regarding items inside the containers.
 */
public class AllGroupsLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllGroupsLayout.class);

    private final boolean debug;
    private final LayoutConfig layoutConfig;

    public AllGroupsLayout(boolean debug, @NonNull final LayoutConfig layoutConfig) {
        this.debug = debug;
        this.layoutConfig = Objects.requireNonNull(layoutConfig);
    }

    /**
     * Renders the landscape using {@link FastOrganicLayout}.
     */
    public LayoutedComponent getRendered(Landscape landscape, Map<URI, Group> groups, Map<URI, SubLayout> subgraphs) {

        LOGGER.debug("Subgraphs sequence: {}", subgraphs);
        Map<Group, LayoutedComponent> groupNodes = new LinkedHashMap<>();
        List<Item> items = new ArrayList<>();
        var sorted = new TreeMap<>(groups);
        sorted.forEach((groupName, groupItem) -> {

            //do not layout the group if empty
            if (groupItem.getChildren().isEmpty()) {
                return;
            }

            SubLayout subLayout = subgraphs.get(groupName);
            if (subLayout == null)
                return;
            LayoutedComponent groupGeometry = subLayout.getOuterBounds();
            groupNodes.put(groupItem, groupGeometry);
            items.addAll(groupItem.getChildren());
        });
        if (debug) LOGGER.debug("Group node sequence: {}", groupNodes);

        addVirtualEdgesBetweenGroups(items, groupNodes);


        var layout = new FastOrganicLayout(
                new ArrayList<>(groupNodes.values()),
                new CollisionRegardingForces(layoutConfig.getGroupMinDistanceLimit(), layoutConfig.getGroupMaxDistanceLimit()),
                LayoutConfig.GROUP_LAYOUT_INITIAL_TEMP
        );
        layout.setDebug(debug);

        layout.execute();
        if (debug) {
            try {
                String name = landscape.getName();
                layout.getLayoutLogger().traceLocations(new File(RootPath.get() + "/src/test/dump/" + name + ".svg"));
                layout.getLayoutLogger().dump(new File(RootPath.get() + "/src/test/dump/" + name + ".txt"));
            } catch (IOException e) {
                LOGGER.warn("Failed to write debug information", e);
            }
        }
        layout.assertMinDistanceIsKept(layoutConfig.getGroupMinDistanceLimit());
        if (debug) LOGGER.debug("AllGroupsLayout bounds: {}", layout.getNodes());

        return LayoutedComponent.from(landscape, layout.getNodes());
    }


    /**
     * Virtual edges between group containers enable organic layout of groups.
     */
    private void addVirtualEdgesBetweenGroups(List<Item> items, Map<Group, LayoutedComponent> groupNodes) {

        GroupConnections groupConnections = new GroupConnections();

        items.forEach(item -> {
            final String group = item.getParent().getIdentifier();
            LayoutedComponent groupNode = findGroupBounds(item.getParent(), groupNodes);

            item.getRelations().forEach(relationItem -> {
                Item targetItem = (Item) relationItem.getTarget();
                if (targetItem == null) {
                    LOGGER.warn("Virtual connections: No target in relation item {}", relationItem);
                    return;
                }

                String targetGroup = targetItem.getParent().getIdentifier();
                LayoutedComponent targetGroupNode = findGroupBounds(targetItem.getParent(), groupNodes);

                if (groupConnections.canConnect(group, targetGroup)) {
                    groupNode.getOpposites().add(targetGroupNode.getComponent());
                    targetGroupNode.getOpposites().add(groupNode.getComponent());
                    groupConnections.connect(group, targetGroup, "Virtual connection between ");
                }
            });
        });
    }

    private LayoutedComponent findGroupBounds(Group group, Map<Group, LayoutedComponent> groupNodes) {
        return Optional.ofNullable(groupNodes.get(group))
                .orElseThrow(() -> new RuntimeException(String.format("Group %s not found.", group)));
    }

}
