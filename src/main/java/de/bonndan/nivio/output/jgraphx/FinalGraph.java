package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.Color;
import de.bonndan.nivio.output.RenderedArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

import static de.bonndan.nivio.output.Color.getGroupColor;
import static de.bonndan.nivio.output.map.MapFactory.DEFAULT_ICON_SIZE;

/**
 * This class is responsible for rendering services and groups nicely with bells and whistles.
 *
 * The output is now mainly used as basis for further processing of landscape items.
 */
public class FinalGraph implements RenderedArtifact<mxGraph, mxCell> {

    public static final int GRID_SIZE = 20;
    private static final String CUSTOM_TYPE = "custom";

    private final Logger logger = LoggerFactory.getLogger(FinalGraph.class);
    private final Map<Item, mxCell> itemVertexes = new LinkedHashMap<>();
    private mxStylesheet stylesheet;
    private mxGraph graph;
    private final Map<Group, mxCell> groups = new LinkedHashMap<>();

    public FinalGraph() {
    }

    public mxGraph render(AllGroupsGraph allGroupsGraph, Map<String, GroupGraph> subgraphs) {

        graph = new mxGraph();
        graph.setGridEnabled(true);
        graph.setGridSize(GRID_SIZE);
        graph.setHtmlLabels(true);
        stylesheet = graph.getStylesheet();

        final List<Item> items = new ArrayList<>();

        allGroupsGraph.getGroupObjects().forEach((group, mxCell) -> {

            mxGeometry groupGeo = mxCell.getGeometry();
            mxCell groupContainer = (mxCell) graph.insertVertex(
                    graph.getDefaultParent(),
                    group.getIdentifier(), group.getIdentifier(),
                    groupGeo.getX(),
                    groupGeo.getY(),
                    groupGeo.getWidth(),
                    groupGeo.getHeight(),
                    getGroupStyle(group)
            );
            groups.put(group, groupContainer);

            GroupGraph groupGraph = subgraphs.get(group.getIdentifier());
            groupGraph.getServiceVertexesWithRelativeOffset().forEach((service, offset) -> {
                itemVertexes.put(
                        (Item) service,
                        addItemVertex(offset, groupContainer, (Item)service)
                );
                items.add((Item) service);
            });

            resizeContainer(groupContainer);
        });

        addDataFlow(items);

        return graph;
    }

    @Override
    public mxGraph getRendered() {
        return graph;
    }

    /**
     * Adds dataflow edges.
     */
    private void addDataFlow(List<Item> items) {

        items.forEach(service -> service.getRelations().forEach(rel -> {

            if (rel.getSource().equals(rel.getTarget()))
                return;

            String astyle;
            if (RelationType.PROVIDER.equals(rel.getType())) {
                astyle = getProviderEdgeStyle(rel.getSource());
            } else {
                astyle = getDataFlowStyle(service);
            }

            String id = "df_" + service.getIdentifier() + "_" + rel.getTarget();
            logger.debug("Adding relation " + id);
            graph.insertEdge(graph.getDefaultParent(), id, rel.getFormat(),
                    itemVertexes.get(rel.getSource()),
                    itemVertexes.get(rel.getTarget()),
                    astyle
            );

        }));
    }

    private void resizeContainer(mxCell cell) {
        Object[] childCells = Arrays.stream(graph.getChildCells(cell)).filter(o -> ((mxCell) o).isVertex()).toArray();

        double amount = DEFAULT_ICON_SIZE * 0.8;
        cell.getGeometry().grow(amount);
        Arrays.stream(childCells).forEach(o -> {
            mxCell child = ((mxCell) o);
            if (StringUtils.isEmpty(child.getValue()))
                return;
            mxGeometry childGeo = child.getGeometry();
            childGeo.setX(childGeo.getX() + amount);
            childGeo.setY(childGeo.getY() + amount);
        });
    }

    private mxCell addItemVertex(mxPoint offset, mxCell parent, Item landscapeItem) {

        String style = getItemStyle(landscapeItem);

        String name = StringUtils.isEmpty(landscapeItem.getName()) ? landscapeItem.getIdentifier() : landscapeItem.getName();
        if (Lifecycle.isPlanned(landscapeItem)) {
            name = name + "\n(planned)";
        }

        return (mxCell) graph.insertVertex(
                parent,
                landscapeItem.getFullyQualifiedIdentifier().toString(),
                name,
                graph.snap(offset.getX()),
                graph.snap(offset.getY()),
                DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE,
                style
        );
    }

    private String getItemStyle(LandscapeItem landscapeItem) {
        String style = getBaseStyle((Item) landscapeItem) + ";" + "type=" + landscapeItem.getType()
                + ";group=" + landscapeItem.getGroup() + ";"
                + "strokeColor=" + getGroupColor((Item) landscapeItem) + ";";
        if (Lifecycle.isPlanned(landscapeItem)) {
            style = style + mxConstants.STYLE_DASHED + "=1";
        }
        return style;
    }

    private String getBaseStyle(Item item) {
        String type = StringUtils.isEmpty(item.getType()) ? CUSTOM_TYPE : item.getType() ;

        if (stylesheet.getStyles().containsKey(type)) {
            return type;
        }

        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_SHAPE, mxCircularImageShape.NAME);
        style.put(mxConstants.STYLE_STROKEWIDTH, 3);
        style.put(mxConstants.STYLE_FILLCOLOR, "white");
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP); //decreases space between label and img
        style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
        style.put(mxConstants.STYLE_FONTCOLOR, "black");

        stylesheet.putCellStyle(type, style);

        return type;
    }

    private String getDataFlowStyle(Item item) {
        String groupColor = getGroupColor(item);
        String style = mxConstants.STYLE_STROKEWIDTH + "=4;"
                + mxConstants.STYLE_VERTICAL_LABEL_POSITION + "=bottom;"
                + mxConstants.STYLE_SHAPE + "=" + CurvedShape.KEY + ";"
                + mxConstants.STYLE_EDGE + "=" + CurvedEdgeStyle.KEY + ";"
                + mxConstants.STYLE_LABEL_BACKGROUNDCOLOR + "=#" + groupColor + ";"
                + mxConstants.STYLE_FONTCOLOR + "=black;"
                + getStrokeColor(item);

        if (Lifecycle.isPlanned(item) || Lifecycle.isEndOfLife(item)) {
            style = style + mxConstants.STYLE_DASHED + "=1";
        }

        return style;
    }

    private String getGroupStyle(Group group) {

        String groupColor = Color.getGroupColor(group);
        String lightened = Color.lighten(groupColor);
        return "type=group;groupColor=" + groupColor + ";"
                + "strokeColor=" + groupColor + ";"
                + "strokeWidth=1;"
                + "rounded=1;"
                + mxConstants.STYLE_FILLCOLOR + "=" + lightened + ";"
                + mxConstants.STYLE_VERTICAL_ALIGN + "=" + mxConstants.ALIGN_BOTTOM + ";"
                + mxConstants.STYLE_VERTICAL_LABEL_POSITION + "=" + mxConstants.ALIGN_TOP + ";"
                + mxConstants.STYLE_FONTCOLOR + "=#" + groupColor + ";";
    }

    private String getProviderEdgeStyle(Item provider) {
        String style = mxConstants.STYLE_STROKEWIDTH + "=2;"
                + mxConstants.STYLE_ENDARROW + "=oval;"
                + mxConstants.STYLE_STARTARROW + "=false;"
                + getStrokeColor(provider);

        if (Lifecycle.isPlanned(provider) || Lifecycle.isEndOfLife(provider)) {
            style = style + mxConstants.STYLE_DASHED + "=1";
        }

        return style;
    }

    private String getStrokeColor(Item item) {
        String groupColor = getGroupColor(item);
        logger.debug("Dataflow stroke color {} for service {} group {}", groupColor, item.getIdentifier(), item.getGroup());
        return mxConstants.STYLE_STROKECOLOR + "=#" + groupColor + ";";
    }

    public Map<Group, mxCell> getGroupObjects() {
        return groups;
    }

    public Map<Item, mxCell> getItemObjects() {
        return itemVertexes;
    }
}
