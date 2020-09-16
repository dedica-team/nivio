package de.bonndan.nivio.output.layout;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Renders a graph of group containers only, not regarding items inside the containers.
 */
public class AllGroupsLayout {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllGroupsLayout.class);
    public static final int FORCE_CONSTANT = 350;
    public static final int MAX_DISTANCE_LIMIT = 1000;

    private final Map<Group, LayoutedComponent> groupNodes = new LinkedHashMap<>();
    private final FastOrganicLayout layout;
    private final Landscape landscape;

    public AllGroupsLayout(Landscape landscape, Map<String, Group> groups, Map<String, SubLayout> subgraphs) {
        this.landscape = landscape;

        LOGGER.debug("Subgraphs sequence: {}", subgraphs);

        List<LandscapeItem> items = new ArrayList<>();
        groups.forEach((groupName, groupItem) -> {

            //do not layout the default group if empty
            if (Group.COMMON.equals(groupName)&& groupItem.getItems().size() == 0) {
                return;
            }

            SubLayout subLayout = subgraphs.get(groupName);
            if (subLayout == null)
                return;
            LayoutedComponent groupGeometry = subLayout.getOuterBounds();
            groupNodes.put(groupItem, groupGeometry);
            items.addAll(groupItem.getItems());
        });
        LOGGER.debug("Group node sequence: {}", groupNodes);

        addVirtualEdgesBetweenGroups(items);

        layout = new FastOrganicLayout(new ArrayList<>(groupNodes.values()));
        //layout.setDebug(true);
        layout.setForceConstant(FORCE_CONSTANT);
        layout.setMaxDistanceLimit(MAX_DISTANCE_LIMIT);

        layout.configure(landscape.getConfig().getGroupLayoutConfig());

        layout.execute();
        LOGGER.debug("AllGroupsLayout bounds: {}", layout.getBounds());
    }


    /**
     * Virtual edges between group containers enable organic layout of groups.
     */
    private void addVirtualEdgesBetweenGroups(List<LandscapeItem> items) {

        GroupConnections groupConnections = new GroupConnections();

        items.forEach(item -> {
            final String group;
            if (StringUtils.isEmpty(item.getGroup())) {
                LOGGER.warn("Item {} has no group, using " + Group.COMMON, item);
                group = Group.COMMON;
            } else {
                group = item.getGroup();
            }
            LayoutedComponent groupNode = findGroupBounds(group);

            item.getRelations().forEach(relationItem -> {
                Item targetItem = (Item) relationItem.getTarget();
                if (targetItem == null) {
                    LOGGER.warn("Virtual connections: No target in relation item {}", relationItem);
                    return;
                }

                String targetGroup = targetItem.getGroup() == null ? Group.COMMON : targetItem.getGroup();
                LayoutedComponent targetGroupNode = findGroupBounds(targetGroup);

                if (groupConnections.canConnect(group, targetGroup)) {
                    groupNode.getOpposites().add(targetGroupNode.getComponent());
                    targetGroupNode.getOpposites().add(groupNode.getComponent());
                    groupConnections.connect(group, targetGroup, "Virtual connection between ");
                }
            });
        });
    }

    private LayoutedComponent findGroupBounds(String group) {

        if (StringUtils.isEmpty(group))
            group = Group.COMMON;

        String finalGroup = group;
        return groupNodes.entrySet().stream()
                .filter(entry -> finalGroup.equals(entry.getKey().getIdentifier()))
                .findFirst().map(Map.Entry::getValue).orElseThrow(() -> new RuntimeException("Group " + finalGroup + " not found."));
    }

    /**
     * Returns the layouted landscape.
     */
    public LayoutedComponent getRendered() {
        return layout.getOuterBounds(landscape);
    }

}
