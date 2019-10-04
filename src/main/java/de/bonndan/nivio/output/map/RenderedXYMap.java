package de.bonndan.nivio.output.map;

import de.bonndan.nivio.output.jgraphx.FinalGraph;

import java.util.ArrayList;
import java.util.List;

public class RenderedXYMap {

    public final List<XYMapItem> items = new ArrayList<>();
    public final List<XYMapItem> groups = new ArrayList<>();

    public Integer width;
    public Integer height;

    public static RenderedXYMap from(FinalGraph finalGraph) {
        RenderedXYMap renderedMap = new RenderedXYMap();
        finalGraph.getItemVertexes().forEach((service, mxCell) -> renderedMap.items.add(new XYMapItem(service, mxCell)));
        finalGraph.getGroupVertexes().forEach((groupName, mxCell) -> renderedMap.groups.add(new XYMapItem(groupName, mxCell)));

        return renderedMap;
    }
}
