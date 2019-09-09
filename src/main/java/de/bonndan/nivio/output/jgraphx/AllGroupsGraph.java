package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.landscape.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Renders a graph of group containers only, not regarding services inside the containers.
 */
public class AllGroupsGraph {

    private static final Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);

    private final mxGraph graph;
    private final Map<String, mxCell> groupNodes = new HashMap<>();

    public AllGroupsGraph(LandscapeConfig config, Groups groups, Map<String, GroupGraph> subgraphs) {

        graph = new mxGraph();

        List<ServiceItem> services = new ArrayList<>();
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
            services.addAll(serviceItems);
        });

        addVirtualEdgesBetweenGroups(services);

        mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
        Optional.ofNullable(config.getJgraphx().getMaxIterations())
                .ifPresent(layout::setMaxIterations);
        Optional.ofNullable(config.getJgraphx().getInitialTemp())
                .ifPresent(layout::setInitialTemp);
        //longer edges, containers are enlarged later
        Optional.ofNullable(config.getJgraphx().getForceConstantFactor())
                .ifPresent(f -> layout.setForceConstant(layout.getForceConstant() * f));

        Optional.ofNullable(config.getJgraphx().getMinDistanceLimitFactor())
                .ifPresent(f -> layout.setMinDistanceLimit(layout.getMinDistanceLimit() * f));


        layout.execute(graph.getDefaultParent());
    }


    /**
     * Virtual edges between group containers enable organic layout of groups.
     *
     * @param services
     */
    private void addVirtualEdgesBetweenGroups(List<ServiceItem> services) {
        HashMap<mxCell, mxCell> groupConnections = new HashMap<>();
        services.forEach(service -> {
            String group = service.getGroup();
            mxCell groupNode = groupNodes.get(group);

            BiFunction<mxCell, mxCell, Boolean> canLink = (ownGroup, otherGroup) -> {

                if (ownGroup == null)
                    return false;
                if (otherGroup == null)
                    return false;
                if (ownGroup == otherGroup)
                    return false;

                if (groupConnections.get(ownGroup) == otherGroup)
                    return false;
                if (groupConnections.get(otherGroup) == ownGroup)
                    return false;

                return true;
            };

            //provider
            ((Service) service).getProvidedBy().forEach(provider -> {
                String pGroup = provider.getGroup() == null ? Groups.COMMON : provider.getGroup();
                mxCell pGroupNode = groupNodes.get(pGroup);
                if (canLink.apply(groupNode, pGroupNode)) {
                    graph.insertEdge(graph.getDefaultParent(), "", "", groupNode, pGroupNode,
                            mxConstants.STYLE_STROKEWIDTH + "=2;" + mxConstants.STYLE_STROKECOLOR + "=red;"
                    );
                    groupConnections.put(groupNode, pGroupNode);
                    logger.info("************ Virtual provider connection between " + service.getGroup() + " and " + pGroup);
                }
            });

            //dataflow
            service.getDataFlow().forEach(dataFlowItem -> {
                String target = dataFlowItem.getTarget();
                if (target == null) return;
                ServiceItem targetItem = ServiceItems.find(target, null, services);
                if (targetItem == null) return;

                String pGroup = targetItem.getGroup() == null ? Groups.COMMON : targetItem.getGroup();
                mxCell pGroupNode = groupNodes.get(pGroup);

                if (canLink.apply(groupNode, pGroupNode)) {
                    graph.insertEdge(graph.getDefaultParent(), "", "", groupNode, pGroupNode,
                            mxConstants.STYLE_STROKEWIDTH + "=none;" + mxConstants.STYLE_OPACITY + "=1;"
                    );
                    groupConnections.put(groupNode, pGroupNode);
                    logger.info("************ Virtual Dataflow connection between " + groupNode + " and " + pGroupNode);
                }
            });
        });

        //add connections from unconnected groups to each other group to keep it at distance
        groupNodes.forEach((s, groupNode) -> {
            boolean connected = groupConnections.entrySet().stream()
                    .anyMatch(entry -> entry.getKey().equals(groupNode) || entry.getValue().equals(groupNode));

            if (connected)
                return;

            groupNodes.forEach((s1, other) -> {
                if (other.equals(groupNode))
                    return;
                graph.insertEdge(graph.getDefaultParent(), "", "distance", groupNode, other,
                        mxConstants.STYLE_STROKEWIDTH + "=2;" + mxConstants.STYLE_STROKECOLOR + "=blue;"
                );
                groupConnections.put(groupNode, other);
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
}
