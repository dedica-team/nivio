package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import de.bonndan.nivio.landscape.*;
import de.bonndan.nivio.output.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static de.bonndan.nivio.landscape.Status.GREEN;
import static de.bonndan.nivio.landscape.Status.UNKNOWN;

public class JGraphXRenderer implements Renderer {
    private final int DEFAULT_ICON_SIZE = 40;

    private static final String[] KNOWN_ICONS = new String[]{
            "api",
            "cache",
            "database",
            "dataflow",
            "firewall",
            "humanuser",
            "interface",
            "keyvaluestore",
            "loadbalancer",
            "lock",
            "messagequeue",
            "mobileclient",
            "server",
            "service",
            "webservice",
    };

    private Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);
    private Map<Service, Object> serviceVertexes = new HashMap<>();
    private mxStylesheet stylesheet;
    private mxGraph graph;

    @Override
    public String render(Landscape landscape) {
        return null;
    }

    @Override
    public void render(Landscape landscape, File file) throws IOException {
        graph = new mxGraph();
        graph.setHtmlLabels(true);

        stylesheet = graph.getStylesheet();
        mxGraphics2DCanvas.putShape("circularImage", new mxCircularImageShape());

        graph.getModel().beginUpdate();
        try {
            render(graph, landscape);
        } finally {

            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
            layout.setOrientation(SwingConstants.SOUTH);
            layout.setInterRankCellSpacing(4 * DEFAULT_ICON_SIZE);
            layout.setIntraCellSpacing(3 * DEFAULT_ICON_SIZE);
            layout.execute(graph.getDefaultParent());

            graph.getModel().endUpdate();

            //render extra stuff per service
            serviceVertexes.entrySet().forEach(this::renderExtras);


            //dataflow renderer after hierarchy layout
            landscape.getServices().forEach(service -> service.getDataFlow().forEach(df -> {

                if (df.getSource().equals(df.getTarget()))
                    return;

                String id = "df_" + service.getIdentifier() + df.getTarget();
                logger.info("Adding dataflow " + id);
                ServiceItem target = ServiceItems.find(FullyQualifiedIdentifier.from(df.getTarget()), landscape.getServices());
                graph.insertEdge(graph.getDefaultParent(), id, df.getFormat(), serviceVertexes.get(service), serviceVertexes.get((Service) target),
                        mxConstants.STYLE_STROKECOLOR + "=#" + getGroupColor(service) + ";"
                                + mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_CURVE + ";"
                );
            }));

            //draw vertexes above edges
            graph.orderCells(false, serviceVertexes.values().toArray());

        }

        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);

        ImageIO.write(image, "PNG", file);

    }

    private void render(mxGraph graph, Landscape landscape) {
        Object parent = graph.getDefaultParent();

        //add all nodes
        landscape.getServices().forEach(service -> {

            // "ROUNDED;strokeColor=red;fillColor=green"
            String statusColor = getStatusColor(service);
            String style = getBaseStyle(service) + ";";
            if (!Status.UNKNOWN.toString().equals(statusColor)) {
                style += mxConstants.STYLE_FILLCOLOR + "=" + statusColor + ";";
            } else {
                style += mxConstants.STYLE_FILLCOLOR + "=white;";
            }
            style += "strokeColor=" + de.bonndan.nivio.util.Color.nameToRGB(service.getGroup(), "gray") + ";strokeWidth=3;";

            Object v1 = graph.insertVertex(
                    parent,
                    service.getFullyQualifiedIdentifier().toString(),
                    StringUtils.isEmpty(service.getName()) ? service.getIdentifier() : service.getName(),
                    20, 20, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE,
                    style
            );
            serviceVertexes.put(service, v1);
        });

        //provider edges
        landscape.getServices().forEach(service -> {
            service.getProvidedBy().forEach(provider -> {

                //ingress: inverse
                if (provider.getLayer().equals(ServiceItem.LAYER_INGRESS)) {
                    String style = mxConstants.STYLE_STROKEWIDTH + "=3;"
                            + mxConstants.STYLE_ENDARROW + "=oval;"
                            + mxConstants.STYLE_STARTARROW + "=false;"
                            + mxConstants.STYLE_STROKECOLOR + "=#" + getGroupColor(service) + ";";
                    graph.insertEdge(
                            parent, null, "",
                            serviceVertexes.get(service),
                            serviceVertexes.get(provider),
                            style
                    );
                    return;
                }

                String style = mxConstants.STYLE_STROKEWIDTH + "=3;"
                        + mxConstants.STYLE_ENDARROW + "=false;"
                        + mxConstants.STYLE_STARTARROW + "=oval;"
                        + mxConstants.STYLE_STROKECOLOR + "=#" + getGroupColor(service) + ";";
                graph.insertEdge(
                        parent, null, "",
                        serviceVertexes.get(provider),
                        serviceVertexes.get(service),
                        style
                );
            });
        });
    }

    private void renderExtras(Map.Entry<Service, Object> entry) {
        mxCell cell = (mxCell) entry.getValue();
        mxRectangle cellBounds = graph.getCellBounds(cell);
        double centerX = cellBounds.getCenterX();
        double centerY = cellBounds.getCenterY();
        Service service = entry.getKey();
        List<StatusItem> displayed = service.getStatuses().stream()
                .filter(item -> !UNKNOWN.equals(item.getStatus()))
                .sorted((statusItem, t1) -> {
                    if (statusItem.getStatus().equals(t1.getStatus()))
                        return 0;
                    return statusItem.getStatus().isHigherThan(t1.getStatus()) ? -1 : 1;
                }).collect(Collectors.toList());

        //statuses at left
        int statusBoxSize = 15;
        double statusOffsetX = cellBounds.getX() - 2.5 * statusBoxSize;
        AtomicReference<Double> statusOffsetY = new AtomicReference<>(cellBounds.getY());
        displayed.forEach(statusItem -> {
            graph.insertVertex(graph.getDefaultParent(), null,
                    statusItem.getLabel().toUpperCase().substring(0, 3),
                    statusOffsetX, statusOffsetY.get(), statusBoxSize * 2, statusBoxSize,
                    mxConstants.STYLE_FILLCOLOR + "=" + statusItem.getStatus().toString() + ";"
                            + mxConstants.STYLE_FONTCOLOR + "=black"
            );
            statusOffsetY.updateAndGet(v -> v + statusBoxSize);
        });

        //interfaces
        int intfBoxSize = 10;
        double intfOffsetX = cellBounds.getX() + cellBounds.getWidth() + 1.5 * intfBoxSize;
        AtomicReference<Double> intfOffsetY = new AtomicReference<>(cellBounds.getY());
        String intfStyle = mxConstants.STYLE_SHAPE + "=" + mxConstants.SHAPE_IMAGE + ";"
                + mxConstants.STYLE_FONTCOLOR + "=black;"
                + mxConstants.STYLE_LABEL_POSITION + "=right;";
        service.getInterfaces().forEach(intf -> {
            Object v1 = graph.insertVertex(graph.getDefaultParent(), null,
                    intf.getFormat(),
                    intfOffsetX, intfOffsetY.get(), intfBoxSize, intfBoxSize,
                    intfStyle
            );
            intfOffsetY.updateAndGet(v -> v + intfBoxSize * 1.5);

            graph.insertEdge(
                    graph.getDefaultParent(), null, "",
                    cell,
                    v1,
                    mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_OVAL+ ";"
                            + mxConstants.STYLE_STROKEWIDTH + "=2;"
                            + mxConstants.STYLE_STROKECOLOR + "=#" + getGroupColor(service) + ";"
            );
        });
    }

    private String getStatusColor(Service service) {
        var ref = new Object() {
            Status current = Status.UNKNOWN;
        };

        service.getStatuses().forEach(statusItem -> {
            if (statusItem.getStatus().isHigherThan(ref.current))
                ref.current = statusItem.getStatus();
        });

        return ref.current.toString();
    }

    private String getGroupColor(Service service) {
        return de.bonndan.nivio.util.Color.nameToRGB(service.getGroup(), "gray");
    }

    private String getIcon(Service service) {
        if (StringUtils.isEmpty(service.getType()))
            return "service";

        //fallback to service
        if (!Arrays.asList(KNOWN_ICONS).contains(service.getType().toLowerCase()))
            return "service";

        return service.getType().toLowerCase();
    }

    private String getBaseStyle(Service service) {
        String type = getIcon(service);

        if (!stylesheet.getStyles().containsKey(type)) {
            Hashtable<String, Object> style = new Hashtable<String, Object>();
            style.put(mxConstants.STYLE_SHAPE, "circularImage");
            style.put(mxConstants.STYLE_IMAGE, "http://localhost:8080/icons/" + type + ".png");
            style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM);
            style.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#666");
            style.put(mxConstants.STYLE_FONTCOLOR, "white");

            stylesheet.putCellStyle(type, style);
        }

        return type;
    }
}
