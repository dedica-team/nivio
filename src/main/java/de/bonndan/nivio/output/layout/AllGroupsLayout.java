package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.util.RootPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Renders a graph of group containers only, not regarding items inside the containers.
 */
public class AllGroupsLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllGroupsLayout.class);

    public static final int MAX_DISTANCE_LIMIT = 1000;

    //results in more iterations and better layouts for larger graphs
    public static final int INITIAL_TEMP = 300 * 3;

    private final boolean debug;

    public AllGroupsLayout(boolean debug) {
        this.debug = debug;
    }

    /**
     * Renders the landscape using {@link FastOrganicLayout}.
     */
    public LayoutedComponent getRendered(Landscape landscape, Map<String, Group> groups, Map<String, SubLayout> subgraphs) {

        LOGGER.debug("Subgraphs sequence: {}", subgraphs);
        Map<Group, LayoutedComponent> groupNodes = new LinkedHashMap<>();
        List<Item> items = new ArrayList<>();
        var sorted = new TreeMap<>(groups);
        sorted.forEach((groupName, groupItem) -> {

            //do not layout the group if empty
            if (groupItem.getItems().isEmpty()) {
                return;
            }

            SubLayout subLayout = subgraphs.get(groupName);
            if (subLayout == null)
                return;
            LayoutedComponent groupGeometry = subLayout.getOuterBounds();
            groupNodes.put(groupItem, groupGeometry);
            items.addAll(landscape.getItems().retrieve(groupItem.getItems()));
        });
        if (debug) LOGGER.debug("Group node sequence: {}", groupNodes);

        addVirtualEdgesBetweenGroups(items, groupNodes);

        int minDistanceLimit = Hex.HEX_SIZE / 2;
        var layout = new FastOrganicLayout(
                new ArrayList<>(groupNodes.values()),
                new CollisionRegardingForces(minDistanceLimit, MAX_DISTANCE_LIMIT),
                INITIAL_TEMP
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
        layout.assertMinDistanceIsKept(minDistanceLimit);
        if (debug) LOGGER.debug("AllGroupsLayout bounds: {}", layout.getNodes());

        return LayoutedComponent.from(landscape, layout.getNodes());
    }


    /**
     * Virtual edges between group containers enable organic layout of groups.
     */
    private void addVirtualEdgesBetweenGroups(List<Item> items, Map<Group, LayoutedComponent> groupNodes) {

        GroupConnections groupConnections = new GroupConnections();

        items.forEach(item -> {
            final String group = item.getGroup();
            LayoutedComponent groupNode = findGroupBounds(group, groupNodes);

            item.getRelations().forEach(relationItem -> {
                Item targetItem = relationItem.getTarget();
                if (targetItem == null) {
                    LOGGER.warn("Virtual connections: No target in relation item {}", relationItem);
                    return;
                }

                String targetGroup = targetItem.getGroup();
                LayoutedComponent targetGroupNode = findGroupBounds(targetGroup, groupNodes);

                if (groupConnections.canConnect(group, targetGroup)) {
                    groupNode.getOpposites().add(targetGroupNode.getComponent());
                    targetGroupNode.getOpposites().add(groupNode.getComponent());
                    groupConnections.connect(group, targetGroup, "Virtual connection between ");
                }
            });
        });
    }

    private LayoutedComponent findGroupBounds(String group, Map<Group, LayoutedComponent> groupNodes) {
        return groupNodes.entrySet().stream()
                .filter(entry -> group.equals(entry.getKey().getIdentifier()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new RuntimeException(String.format("Group %s not found.", group)));
    }

}
