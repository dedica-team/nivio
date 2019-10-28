package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.*;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Renders a graph of group containers only, not regarding items inside the containers.
 */
public class AllGroupsGraph {

    private static final Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);

    private final mxGraph graph;
    private final Map<String, mxCell> groupNodes = new HashMap<>();

    public AllGroupsGraph(LandscapeConfig config, Groups groups, Map<String, GroupGraph> subgraphs) {

        graph = new mxGraph();

        List<LandscapeItem> items = new ArrayList<>();
        groups.getAll().forEach((groupName, serviceItems) -> {

            mxRectangle groupGeometry = subgraphs.get(groupName).getBounds();
            mxCell groupnode = (mxCell) graph.insertVertex(
                    graph.getDefaultParent(),
                    groupName,
                    groupName,
                    0, 0,
                    groupGeometry.getWidth(),
                    groupGeometry.getHeight(),
                    ""
            );
            groupNodes.put(groupName, groupnode);
            items.addAll(serviceItems);
        });

        addVirtualEdgesBetweenGroups(items);

        mxOrganicLayout layout = new mxOrganicLayout(graph);
        Optional.ofNullable(config.getJgraphx().getMaxIterations())
                .ifPresent(layout::setMaxIterations);
        layout.setEdgeLengthCostFactor(layout.getEdgeLengthCostFactor() * 0.001); //edges tend to be longer

        layout.setApproxNodeDimensions(false);


        //edges much longer, good since we enlarge groups with padding
        layout.setAverageNodeArea(layout.getAverageNodeArea() * 25);

        //slighty better layout
        layout.setTriesPerCell(Optional.ofNullable(config.getJgraphx().getTriesPerCell()).orElse(16));

        Optional.ofNullable(config.getJgraphx().getMinDistanceLimitFactor())
                .ifPresent(f -> layout.setMinDistanceLimit(layout.getMinDistanceLimit() * f));


        layout.execute(graph.getDefaultParent());
    }


    /**
     * Virtual edges between group containers enable organic layout of groups.
     *
     */
    private void addVirtualEdgesBetweenGroups(List<LandscapeItem> items) {

        GroupConnections groupConnections = new GroupConnections();

        items.forEach(service -> {
            String group = service.getGroup();
            mxCell groupNode = groupNodes.get(group);

            //provider
            ((Item) service).getProvidedBy().forEach(provider -> {
                String pGroup = provider.getGroup() == null ? Groups.COMMON : provider.getGroup();
                mxCell providerGroupNode = groupNodes.get(pGroup);
                String providerGroup = providerGroupNode.getId();

                if (groupConnections.canConnect(group, providerGroup)) {
                    graph.insertEdge(graph.getDefaultParent(), "", "provider", groupNode, providerGroupNode,
                            mxConstants.STYLE_STROKEWIDTH + "=2;" + mxConstants.STYLE_STROKECOLOR + "=yellow;"
                    );
                    groupConnections.connect(group, providerGroup, "Virtual provider connection between ");
                }
            });

            //dataflow
            service.getRelations(RelationType.DATAFLOW).forEach(dataFlowItem -> {
                String target = dataFlowItem.getTarget();
                if (target == null) return;
                LandscapeItem targetItem = ServiceItems.find(target, null, items).orElse(null);
                if (targetItem == null) return;

                String targetGroup = targetItem.getGroup() == null ? Groups.COMMON : targetItem.getGroup();
                mxCell targetGroupNode = groupNodes.get(targetGroup);

                if (groupConnections.canConnect(group, targetGroup)) {
                    graph.insertEdge(graph.getDefaultParent(), "", "dataflow", groupNode, targetGroupNode,
                            mxConstants.STYLE_STROKEWIDTH + "=none;" + mxConstants.STYLE_STROKECOLOR + "=red;"
                    );
                    groupConnections.connect(group, targetGroup, "Virtual Dataflow connection between ");
                }
            });
        });

        //add connections from unconnected groups to each other group to keep it at distance
        groupNodes.forEach((s, groupNode) -> {
            boolean connected = groupConnections.isConnected(s);

            if (connected)
                return;
            logger.info("Group {} has no virtual connections {}", s, groupConnections.groupConnections);

            //todo fix, persistence is connected via dataflow
            groupNodes.forEach((s1, other) -> {
                if (other.equals(groupNode))
                    return;
                graph.insertEdge(graph.getDefaultParent(), "", "distance", groupNode, other,
                        mxConstants.STYLE_STROKEWIDTH + "=2;" + mxConstants.STYLE_STROKECOLOR + "=blue;"
                );
                groupConnections.connect(s, s1, "Connecting unconnected group to all ");
            });
        });
    }

    public Map<String, mxCell> getLayoutedGroups() {
        return groupNodes;
    }

    /**
     * For layout debugging.
     */
    public mxGraph getGraph() {
        return graph;
    }

    static class GroupConnections {

        private List<Pair<String, String>> groupConnections = new ArrayList<>();

        boolean isConnected(String group) {
            return groupConnections.stream()
                    .anyMatch(entry -> entry.getKey().equals(group) || entry.getValue().equals(group));
        }

        void connect(String a, String b, String message) {
            logger.info(message + a + " and " + b);
            groupConnections.add(new Pair(a, b));
        }

        boolean canConnect(String a, String b) {
            if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b))
                return false;

            if (a.equals(b))
                return false;

            boolean hasLink = groupConnections.stream()
                    .anyMatch(pair ->
                            (pair.getKey().equals(a) && pair.getValue().equals(b))
                            || (pair.getKey().equals(b) && pair.getValue().equals(a))
                    );

            return !hasLink;
        }
    }
}
