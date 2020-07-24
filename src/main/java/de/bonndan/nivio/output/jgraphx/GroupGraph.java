package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.LandscapeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.bonndan.nivio.output.map.MapFactory.DEFAULT_ICON_SIZE;

/**
 * Graph for one group (for the items INSIDE the group).
 * <p>
 * Uses a simple {@link mxOrganicLayout}.
 */
public class GroupGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupGraph.class);

    private final mxGraph graph;
    private final Map<LandscapeItem, mxCell> serviceVertexes = new LinkedHashMap<>();
    private final String name;

    public GroupGraph(String name, List<Item> items) {
        this.name = name;
        graph = new mxGraph();

        items.forEach(service -> {
            mxCell v1 = (mxCell) graph.insertVertex(graph.getDefaultParent(), service.getIdentifier(), service.getName(),
                    0, 0, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE);
            serviceVertexes.put(service, v1);
        });
        LOGGER.debug("Subgraph {} items: {}", name, serviceVertexes);

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
        NivioMoreStaticLayout layout = new NivioMoreStaticLayout(graph);
        layout.execute(graph.getDefaultParent());
        LOGGER.debug("Subgraph {} layouted items: {}", name, serviceVertexes);
    }

    public mxRectangle getBounds() {
        return graph.getGraphBounds();
    }

    public Map<LandscapeItem, mxPoint> getServiceVertexesWithRelativeOffset() {
        mxRectangle graphBounds = graph.getGraphBounds();
        Map<LandscapeItem, mxPoint> relativeOffsets = new LinkedHashMap<>();
        serviceVertexes.forEach((key, value) -> relativeOffsets.put(
                key,
                new mxPoint(
                        value.getGeometry().getX() - graphBounds.getX(),
                        value.getGeometry().getY() - graphBounds.getY()
                )
        ));
        LOGGER.debug("Subgraph {} relative offsets: {}", name, relativeOffsets);
        return relativeOffsets;
    }
}
