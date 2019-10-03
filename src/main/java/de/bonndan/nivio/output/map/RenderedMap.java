package de.bonndan.nivio.output.map;

import de.bonndan.nivio.output.jgraphx.FinalGraph;

import java.util.ArrayList;
import java.util.List;

public class RenderedMap {

    public final List<MapItem> items = new ArrayList<>();
    public final List<MapItem> groups = new ArrayList<>();

    public static RenderedMap from(FinalGraph finalGraph) {
        RenderedMap renderedMap = new RenderedMap();
        finalGraph.getItemVertexes().forEach((service, mxCell) -> renderedMap.items.add(new XYMapItem(service, mxCell)));
        finalGraph.getGroupVertexes().forEach((groupName, mxCell) -> renderedMap.groups.add(new XYMapItem(groupName, mxCell)));

        return renderedMap;
    }
}
