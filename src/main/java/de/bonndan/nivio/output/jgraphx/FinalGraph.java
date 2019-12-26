package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStyleRegistry;
import com.mxgraph.view.mxStylesheet;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.*;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.util.RootPath;

import de.bonndan.nivio.output.Icon;
import de.bonndan.nivio.output.IconService;
import de.bonndan.nivio.output.LocalServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static de.bonndan.nivio.model.Status.UNKNOWN;
import static de.bonndan.nivio.output.Color.getGroupColor;

/**
 * This class is responsible for rendering services and groups nicely with bells and whistles.
 *
 * It receives
 */
public class FinalGraph implements Rendered<mxGraph, mxCell> {

    private final int DEFAULT_ICON_SIZE = 50;
    private final IconService iconService;

    private Logger logger = LoggerFactory.getLogger(FinalGraph.class);
    private Map<Item, mxCell> itemVertexes = new HashMap<>();
    private mxStylesheet stylesheet;
    private mxGraph graph;
    private Map<Group, mxCell> groups = new HashMap<>();

    public FinalGraph(IconService iconService) {
        this.iconService = iconService;
    }

    public mxGraph render(AllGroupsGraph allGroupsGraph, Map<String, GroupGraph> subgraphs) {

        //circular image
        mxGraphics2DCanvas.putShape(mxCircularImageShape.NAME, new mxCircularImageShape());

        //curved edges, https://stackoverflow.com/questions/22746439/jgraphx-custom-layoult-curved-edges
        mxGraphics2DCanvas.putShape(CurvedShape.KEY, new CurvedShape());
        mxStyleRegistry.putValue(CurvedEdgeStyle.KEY, new CurvedEdgeStyle());

        graph = new mxGraph();

        graph.setHtmlLabels(true);
        stylesheet = graph.getStylesheet();


        //graph.orderCells(true, virtualNodes.toArray());
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
                        addServiceVertex(offset, groupContainer, service)
                );
                items.add((Item) service);
            });

            resizeContainer(groupContainer);
        });

        addDataFlow(items);

        renderExtras(itemVertexes);

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
            logger.info("Adding relation " + id);
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

    private mxCell addServiceVertex(mxPoint offset, mxCell parent, LandscapeItem landscapeItem) {

        String style = getItemStyle(landscapeItem);

        String name = StringUtils.isEmpty(landscapeItem.getName()) ? landscapeItem.getIdentifier() : landscapeItem.getName();
        if (Lifecycle.PLANNED.equals(landscapeItem.getLifecycle())) {
            name = name + "\n(planned)";
        }

        return (mxCell) graph.insertVertex(
                parent,
                landscapeItem.getFullyQualifiedIdentifier().toString(),
                name,
                offset.getX(), offset.getY(),
                DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE,
                style
        );
    }

    private void renderExtras(Map<Item, mxCell> map) {

        map.entrySet().forEach(entry -> {
            mxCell cell = entry.getValue();
            mxRectangle cellBounds = graph.getCellBounds(cell);

            //sort statuses, pick worst
            Item item = entry.getKey();
            Optional<StatusItem> displayed = item.getStatuses().stream()
                    .filter(item1 -> !UNKNOWN.equals(item1.getStatus()) && !Status.GREEN.equals(item1.getStatus()))
                    .min((statusItem, t1) -> {
                        if (statusItem.getStatus().equals(t1.getStatus())) {
                            return statusItem.getLabel().compareToIgnoreCase(t1.getLabel());
                        }
                        return statusItem.getStatus().isHigherThan(t1.getStatus()) ? -1 : 1;
                    });

            //statuses at left
            int statusBoxSize = DEFAULT_ICON_SIZE / 2;
            if (cellBounds == null) {
                logger.warn("Render extras: no cell bounds for {}", item);
                return;
            }

            displayed.ifPresent(statusItem -> {
                cell.setValue(cell.getValue() + "\n(" + statusItem.getLabel() + "!)");
                cell.setStyle(cell.getStyle()
                        + mxConstants.STYLE_STROKECOLOR + "=" + statusItem.getStatus().toString() + ";"
                        + mxConstants.STYLE_STROKEWIDTH + "=" + 4 + ";"
                        + mxConstants.STYLE_IMAGE + "=" + LocalServer.url("/icons/" + statusItem.getStatus().getSymbol() + ".png") + ";"
                );
            });

            //interfaces
            int intfBoxSize = 10;
            double intfOffsetX = cellBounds.getCenterX() - 0.5 * intfBoxSize;
            double intfOffsetY = cellBounds.getCenterY() - 0.5 * intfBoxSize;
            String intfStyle = mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_ELLIPSE + ";"
                    + mxConstants.STYLE_FONTCOLOR + "=black;"
                    + mxConstants.STYLE_LABEL_POSITION + "=right;"
                    + mxConstants.STYLE_ALIGN + "=left;"
                    + mxConstants.STYLE_FILLCOLOR + "=#" + getGroupColor(item) + ";"
                    + mxConstants.STYLE_STROKEWIDTH + "=0;";


            AtomicInteger count = new AtomicInteger(0);
            Stream<InterfaceItem> sorted = item.getInterfaces().stream()
                    .sorted((interfaceItem, t1) -> interfaceItem.getDescription().compareToIgnoreCase(t1.getDescription()));
            sorted.forEach(intf -> {

                double angleInRadians = -1 + count.getAndIncrement() * 0.5;
                double radius = intfBoxSize * 4;
                double x = Math.cos(angleInRadians) * radius;
                double y = Math.sin(angleInRadians) * radius;

                Object v1 = graph.insertVertex(graph.getDefaultParent(), null,
                        intf.getDescription() + " (" + intf.getFormat() + ")",
                        intfOffsetX + x, intfOffsetY + y, intfBoxSize, intfBoxSize,
                        intfStyle
                );

                graph.insertEdge(
                        graph.getDefaultParent(), null, "",
                        cell,
                        v1,
                        mxConstants.STYLE_ENDARROW + "=none;"
                                + mxConstants.STYLE_STROKEWIDTH + "=2;"
                                + mxConstants.STYLE_STROKECOLOR + "=#" + getGroupColor(item) + ";"
                );
            });
        });

    }

    private String getItemStyle(LandscapeItem landscapeItem) {
        String style = getBaseStyle((Item) landscapeItem) + ";" + "type=" + landscapeItem.getType()
                + ";group=" + landscapeItem.getGroup() + ";"
                + "strokeColor=" + getGroupColor((Item) landscapeItem) + ";";
        if (Lifecycle.PLANNED.equals(landscapeItem.getLifecycle())) {
            style = style + mxConstants.STYLE_DASHED + "=1";
        }
        return style;
    }

    private String getBaseStyle(Item item) {
        Icon type;
        if (iconService == null) {
            try {
                type = new Icon(new File(RootPath.get() + "/src/main/resources/static/icons/service.png").toURI().toURL());
            } catch (MalformedURLException e) {
                return "";
            }
        } else {
            type = iconService.getIcon(item);
        }

        if (stylesheet.getStyles().containsKey(type.getUrl().toString())) {
            return type.getUrl().toString();
        }

        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_SHAPE, mxCircularImageShape.NAME);
        style.put(mxConstants.STYLE_STROKEWIDTH, 3);
        style.put(mxConstants.STYLE_FILLCOLOR, "white");
        style.put(mxConstants.STYLE_IMAGE, type.getUrl());
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP); //decreases space between label and img
        style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
        style.put(mxConstants.STYLE_FONTCOLOR, "black");

        stylesheet.putCellStyle(type.getUrl().toString(), style);

        return type.getUrl().toString();
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

        if (Lifecycle.PLANNED.equals(item.getLifecycle()) || Lifecycle.END_OF_LIFE.equals(item.getLifecycle())) {
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

        if (Lifecycle.PLANNED.equals(provider.getLifecycle()) || Lifecycle.END_OF_LIFE.equals(provider.getLifecycle())) {
            style = style + mxConstants.STYLE_DASHED + "=1";
        }

        return style;
    }

    private String getStrokeColor(Item item) {
        Status providerStatus = Status.highestOf(item.getStatuses());
        if (Status.RED.equals(providerStatus))
            return mxConstants.STYLE_STROKECOLOR + "=red;";
        if (Status.ORANGE.equals(providerStatus))
            return mxConstants.STYLE_STROKECOLOR + "=orange;";

        String groupColor = getGroupColor(item);
        logger.info("Dataflow stroke color {} for service {} group {}", groupColor, item.getIdentifier(), item.getGroup());
        return mxConstants.STYLE_STROKECOLOR + "=#" + groupColor + ";";
    }

    public Map<Group, mxCell> getGroupObjects() {
        return groups;
    }

    public Map<Item, mxCell> getItemObjects() {
        return itemVertexes;
    }
}
