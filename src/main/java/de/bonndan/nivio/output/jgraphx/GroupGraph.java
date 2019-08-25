package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.landscape.Service;
import de.bonndan.nivio.landscape.ServiceItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupGraph {
    public final int DEFAULT_ICON_SIZE = 50;
    private final mxGraph graph;
    private final String groupName;
    private Map<ServiceItem, mxCell> serviceVertexes = new HashMap<>();

    public GroupGraph(String groupName, List<ServiceItem> services) {
        this.groupName = groupName;
        graph = new mxGraph();

        services.forEach(service -> {
            mxCell v1 = (mxCell) graph.insertVertex(graph.getDefaultParent(), service.getIdentifier(), service.getName(),
                    0, 0, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE);
            serviceVertexes.put(service, v1);
        });

        //inner group relations
        services.forEach(service -> {
            ((Service) service).getProvidedBy().forEach(provider -> {

                if (service.getGroup().equals(provider.getGroup())) {
                    graph.insertEdge(
                            graph.getDefaultParent(), null, "",
                            serviceVertexes.get(provider),
                            serviceVertexes.get(service),
                            ""
                    );
                }
            });
        });

        //organic layout between group containers
        mxOrganicLayout outer = new mxOrganicLayout(graph);
        outer.execute(graph.getDefaultParent());
        System.out.println(serviceVertexes.toString());
    }

    public mxRectangle getBounds() {
        return graph.getGraphBounds();
    }

    public Map<ServiceItem, mxPoint> getServiceVertexesWithRelativeOffset() {
        mxRectangle graphBounds = graph.getGraphBounds();
        Map<ServiceItem, mxPoint> relativeOffsets = new HashMap<>();
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
