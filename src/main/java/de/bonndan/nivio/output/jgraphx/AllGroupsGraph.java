package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.landscape.Groups;
import de.bonndan.nivio.landscape.Service;
import de.bonndan.nivio.landscape.ServiceItem;
import de.bonndan.nivio.landscape.ServiceItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Renders a graph of group containers only, not regarding services inside the containers.
 */
public class AllGroupsGraph {

    private static final Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);

    private final mxGraph graph;
    private final Map<String, mxCell> groupNodes = new HashMap<>();

    public AllGroupsGraph(Groups groups, Map<String, GroupGraph> subgraphs) {

        graph = new mxGraph();

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
        });

        groups.getAll().forEach((s, serviceItems) -> addVirtualEdgesBetweenGroups(serviceItems));

        mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
        layout.setMaxIterations(1000);
        layout.setInitialTemp(18);
        layout.setForceConstant(layout.getForceConstant() * 4); //longer edges, containers are enlarged later
        layout.execute(graph.getDefaultParent());
    }

    /**
     * Virtual edges between group containers enable organic layout of groups.
     *
     * @param services
     */
    private void addVirtualEdgesBetweenGroups(List<ServiceItem> services) {
        services.forEach(service -> {
            String group = service.getGroup();
            Object groupNode = groupNodes.get(group);
            HashMap<Object, Object> groupConnections = new HashMap<>();

            BiFunction<Object, Object, Boolean> canLink = (ownGroup, otherGroup) -> {

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
                Object pGroupNode = groupNodes.get(pGroup);
                if (canLink.apply(groupNode, pGroupNode)) {
                    graph.insertEdge(graph.getDefaultParent(), "", "", groupNode, pGroupNode,
                            mxConstants.STYLE_STROKEWIDTH + "=2;" + mxConstants.STYLE_STROKECOLOR + "=red;"
                    );
                    groupConnections.put(groupNode, pGroupNode);
                    logger.debug("************ Virtual provider connection between " + groupNode + " and " + pGroupNode);
                }
            });

            //dataflow
            service.getDataFlow().forEach(dataFlowItem -> {
                String target = dataFlowItem.getTarget();
                if (target == null) return;
                ServiceItem targetItem = ServiceItems.find(target, null, services);
                if (targetItem == null) return;

                String pGroup = targetItem.getGroup() == null ? Groups.COMMON : targetItem.getGroup();
                Object pGroupNode = groupNodes.get(pGroup);

                if (canLink.apply(groupNode, pGroupNode)) {
                    graph.insertEdge(graph.getDefaultParent(), "", "", groupNode, pGroupNode,
                            mxConstants.STYLE_STROKEWIDTH + "=none;" + mxConstants.STYLE_OPACITY + "=1;"
                    );
                    groupConnections.put(groupNode, pGroupNode);
                    logger.debug("************ Virtual Dataflow connection between " + groupNode + " and " + pGroupNode);
                }
            });
        });
    }

    public Map<String, mxCell> getLayoutedGroups() {
        return groupNodes;
    }
}
