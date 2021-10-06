package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Renders a graph of group containers only, not regarding items inside the containers.
 */
public class AllGroupsLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllGroupsLayout.class);
    public static final int FORCE_CONSTANT = 300;
    public static final int MAX_DISTANCE_LIMIT = 2000;

    //results in more iterations and better layouts for larger graphs
    public static final int INITIAL_TEMP = 300 * 3;
    public static final int MIN_DISTANCE_LIMIT = 300;

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
            items.addAll(groupItem.getItems());
        });
        if (debug) LOGGER.debug("Group node sequence: {}", groupNodes);

        addVirtualEdgesBetweenGroups(items, groupNodes);

        var layout = new FastOrganicLayout(
                new ArrayList<>(groupNodes.values()),
                MIN_DISTANCE_LIMIT,
                MAX_DISTANCE_LIMIT,
                INITIAL_TEMP,
                landscape.getConfig().getGroupLayoutConfig());
        layout.setDebug(debug);

        layout.execute();
        layout.assertMinDistanceIsKept();
        if (debug) LOGGER.debug("AllGroupsLayout bounds: {}", layout.getNodes());

        return layout.getOuterBounds(landscape);
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
