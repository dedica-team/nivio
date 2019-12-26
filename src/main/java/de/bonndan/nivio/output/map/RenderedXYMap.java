package de.bonndan.nivio.output.map;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.output.Rendered;

import java.util.ArrayList;
import java.util.List;

public class RenderedXYMap {

    public final List<XYMapItem> items = new ArrayList<>();
    public final List<XYMapItem> groups = new ArrayList<>();

    public Integer width;
    public Integer height;

    public static RenderedXYMap from(Rendered<mxGraph, mxCell> rendered) {
        RenderedXYMap renderedMap = new RenderedXYMap();
        rendered.getItemObjects().forEach((item, mxCell) -> renderedMap.items.add(new XYMapItem(item, mxCell)));
        rendered.getGroupObjects().forEach((group, mxCell) -> renderedMap.groups.add(new XYMapItem(group, mxCell)));

        return renderedMap;
    }
}
