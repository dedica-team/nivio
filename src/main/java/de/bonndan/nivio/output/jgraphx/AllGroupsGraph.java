package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.LandscapeConfig;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.RenderedArtifact;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Renders a graph of group containers only, not regarding items inside the containers.
 */
public class AllGroupsGraph implements RenderedArtifact<mxGraph, mxCell> {

    private static final Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);

    private final mxGraph graph;
    private final Map<Group, mxCell> groupNodes = new HashMap<>();

    public AllGroupsGraph(LandscapeConfig config, Map<String, Group> groups, Map<String, GroupGraph> subgraphs) {

        graph = new mxGraph();

        List<LandscapeItem> items = new ArrayList<>();
        groups.forEach((groupName, groupItem) -> {
            List<Item> serviceItems = groupItem.getItems();
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
            groupNodes.put(groupItem, groupnode);
            items.addAll(serviceItems);
        });

        addVirtualEdgesBetweenGroups(items);

        NivioGroupLayout layout = new NivioGroupLayout(graph);
        Optional.ofNullable(config.getJgraphx().getMaxIterations())
                .ifPresent(layout::setMaxIterations);

        Optional.ofNullable(config.getJgraphx().getMinDistanceLimitFactor())
                .ifPresent(f -> layout.setMinDistanceLimit(layout.getMinDistanceLimit() * f));

        layout.execute(graph.getDefaultParent());
        logger.info("AllGroupsGraph bounds: {}", graph.getGraphBounds());
    }


    /**
     * Virtual edges between group containers enable organic layout of groups.
     */
    private void addVirtualEdgesBetweenGroups(List<LandscapeItem> items) {

        GroupConnections groupConnections = new GroupConnections();

        items.forEach(item -> {
            final String group = item.getGroup();
            mxCell groupNode = findGroupCell(group);


            //provider
            ((Item) item).getProvidedBy().forEach(provider -> {
                String pGroup = provider.getGroup() == null ? Group.COMMON : provider.getGroup();
                mxCell providerGroupNode = findGroupCell(pGroup);
                String providerGroup = providerGroupNode.getId();

                if (groupConnections.canConnect(group, providerGroup)) {
                    graph.insertEdge(graph.getDefaultParent(), "", "provider", groupNode, providerGroupNode,
                            mxConstants.STYLE_STROKEWIDTH + "=2;" + mxConstants.STYLE_STROKECOLOR + "=yellow;"
                    );
                    groupConnections.connect(group, providerGroup, "Virtual provider connection between ");
                }
            });

            //dataflow
            List<RelationItem> relations = RelationType.DATAFLOW.filter(item.getRelations());
            relations.forEach(dataFlowItem -> {
                Item targetItem = (Item) dataFlowItem.getTarget();
                if (targetItem == null) return;

                String targetGroup = targetItem.getGroup() == null ? Group.COMMON : targetItem.getGroup();
                mxCell targetGroupNode = findGroupCell(targetGroup);

                if (groupConnections.canConnect(group, targetGroup)) {
                    graph.insertEdge(graph.getDefaultParent(), "", "dataflow", groupNode, targetGroupNode,
                            mxConstants.STYLE_STROKEWIDTH + "=none;" + mxConstants.STYLE_STROKECOLOR + "=red;"
                    );
                    groupConnections.connect(group, targetGroup, "Virtual Dataflow connection between ");
                }
            });
        });
    }

    private mxCell findGroupCell(String group) {

        if (StringUtils.isEmpty(group))
            group = Group.COMMON;

        String finalGroup = group;
        return groupNodes.entrySet().stream()
                .filter(entry -> finalGroup.equals(entry.getKey().getIdentifier()))
                .findFirst().map(Map.Entry::getValue).orElseThrow(() -> new RuntimeException("Group " + finalGroup + " not found."));
    }

    @Override
    public Map<Group, mxCell> getGroupObjects() {
        return groupNodes;
    }

    @Override
    public Map<Item, mxCell> getItemObjects() {
        return null;
    }

    /**
     * For layout debugging.
     */
    public mxGraph getRendered() {
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
            groupConnections.add(new ImmutablePair(a, b));
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
