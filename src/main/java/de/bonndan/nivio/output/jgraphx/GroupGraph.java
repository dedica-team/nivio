package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupGraph {
    public final int DEFAULT_ICON_SIZE = 50;
    private final mxGraph graph;
    private Map<LandscapeItem, mxCell> serviceVertexes = new HashMap<>();

    public GroupGraph(List<Item> items) {
        graph = new mxGraph();

        items.forEach(service -> {
            mxCell v1 = (mxCell) graph.insertVertex(graph.getDefaultParent(), service.getIdentifier(), service.getName(),
                    0, 0, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE);
            serviceVertexes.put(service, v1);
        });

        //inner group relations
        items.forEach(item -> {
            item.getProvidedBy().forEach(provider -> {

                boolean sameGroup = (item.getGroup() == null && provider.getGroup() == null)
                        || item.getGroup().equals(provider.getGroup());
                if (sameGroup) {
                    graph.insertEdge(
                            graph.getDefaultParent(), null, "",
                            serviceVertexes.get(provider),
                            serviceVertexes.get(item),
                            ""
                    );
                }
            });
        });

        //organic layout between group containers
        mxOrganicLayout layout = new mxOrganicLayout(graph);
        layout.setTriesPerCell(16);
        layout.setEdgeLengthCostFactor(layout.getEdgeLengthCostFactor() * 0.1); //edges tend to be longer
        layout.setUseBoundingBox(false);
        layout.execute(graph.getDefaultParent());
    }

    public mxRectangle getBounds() {
        return graph.getGraphBounds();
    }

    public Map<LandscapeItem, mxPoint> getServiceVertexesWithRelativeOffset() {
        mxRectangle graphBounds = graph.getGraphBounds();
        Map<LandscapeItem, mxPoint> relativeOffsets = new HashMap<>();
        serviceVertexes.forEach((key, value) -> relativeOffsets.put(
                key,
                new mxPoint(
                        value.getGeometry().getX() - graphBounds.getX(),
                        value.getGeometry().getY() - graphBounds.getY()
                )
        ));
        return relativeOffsets;
    }
}
