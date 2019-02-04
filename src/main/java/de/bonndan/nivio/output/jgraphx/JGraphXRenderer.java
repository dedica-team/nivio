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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static de.bonndan.nivio.landscape.Status.GREEN;

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



        }

        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);

        ImageIO.write(image, "PNG", file);

    }

    private void render(mxGraph graph, Landscape landscape) {
        Object parent = graph.getDefaultParent();

        //add all nodes
        landscape.getServices().forEach(service -> {

            // "ROUNDED;strokeColor=red;fillColor=green"
            String style = getBaseStyle(service) + ";" + mxConstants.STYLE_FILLCOLOR + "=#" + de.bonndan.nivio.util.Color.nameToRGB(service.getGroup()) + ";";
            String statusColor = getStatusColor(service);
            if (!Status.UNKNOWN.toString().equals(statusColor)) {
                style += "strokeColor=" + statusColor + ";strokeWidth=3; ";
            }

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

                String style = mxConstants.STYLE_STROKEWIDTH + "=3;"
                        + mxConstants.STYLE_ENDARROW + "=false;"
                        + mxConstants.STYLE_STARTARROW + "=oval;"
                        + mxConstants.STYLE_STROKECOLOR + "=#" + de.bonndan.nivio.util.Color.nameToRGB(service.getGroup()) + ";";
                graph.insertEdge(
                        parent, null, "",
                        serviceVertexes.get(provider),
                        serviceVertexes.get(service),
                        style
                );
            });
        });

        //dataflow
        landscape.getServices().forEach(service -> service.getDataFlow().forEach(df -> {

            if (df.getSource().equals(df.getTarget()))
                return;

            String id = "df_" + service.getIdentifier() + df.getTarget();
            logger.info("Adding dataflow " + id);
            ServiceItem target = ServiceItems.find(FullyQualifiedIdentifier.from(df.getTarget()), landscape.getServices());
            graph.insertEdge(parent, id, df.getFormat(), serviceVertexes.get(service), serviceVertexes.get((Service) target),
                    mxConstants.STYLE_STROKECOLOR + "=#" + de.bonndan.nivio.util.Color.nameToRGB(service.getGroup() + ";" + mxConstants.STYLE_DASHED + "=true")
            );
        }));

        //draw vertexes above edges
        graph.orderCells(false, serviceVertexes.values().toArray());
    }

    private void renderExtras(Map.Entry<Service, Object> entry) {
        mxCell cell = (mxCell)entry.getValue();
        mxRectangle cellBounds = graph.getCellBounds(cell);
        double centerX = cellBounds.getCenterX();
        double centerY = cellBounds.getCenterY();
        Service service = entry.getKey();
        List<StatusItem> displayed = service.getStatuses().stream()
                .filter(item -> !GREEN.equals(item.getStatus()))
                .collect(Collectors.toList());

        //statuses at left
        int statusBoxSize = 15;
        double statusOffsetX = cellBounds.getX() - 2* statusBoxSize;
        AtomicReference<Double> statusOffsetY = new AtomicReference<>(cellBounds.getY());
        displayed.forEach(statusItem -> {
            graph.insertVertex(graph.getDefaultParent(), null,
                    statusItem.getLabel().toUpperCase().substring(0, 3),
                    statusOffsetX, statusOffsetY.get(), statusBoxSize * 2, statusBoxSize,
                    mxConstants.STYLE_FILLCOLOR + "=" + statusItem.getStatus().toString());
            statusOffsetY.updateAndGet(v -> v + statusBoxSize);
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
