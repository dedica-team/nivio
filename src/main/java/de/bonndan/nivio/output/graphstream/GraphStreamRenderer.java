package de.bonndan.nivio.output.graphstream;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.Renderer;
import de.bonndan.nivio.util.Color;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.Status.GREEN;


public class GraphStreamRenderer implements Renderer<Graph> {

    private Logger logger = LoggerFactory.getLogger(GraphStreamRenderer.class);

    private Graph graph;
    private SpriteManager spriteManager;

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

    @Override
    public Graph render(LandscapeImpl landscape) {
        graph = new SingleGraph(landscape.getName());
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('" + LocalServer.url("/css/graph.css") + "')");
        graph.addAttribute("layout.quality", 4);

        spriteManager = new SpriteManager(graph);

        Positioner positioner = new Positioner();

        landscape.getItems().forEach(service -> {
            Node n = graph.addNode(service.getIdentifier());
            n.addAttribute("ui.label", StringUtils.isEmpty(service.getName()) ? service.getIdentifier() : service.getName());
            n.addAttribute("ui.class", service.getLayer());
            String style = "fill-color: #" + Color.nameToRGB(service.getGroup()) + "; ";
            String statusColor = getStatusColor(service);
            if (!Status.UNKNOWN.toString().equals(statusColor)) {
                style += "stroke-color: " + statusColor + "; stroke-width: 3px; ";
            }
            n.addAttribute("ui.style", style);

            Sprite icon = spriteManager.addSprite("icon_" + service.getIdentifier());
            icon.setPosition(0, 0, 0);
            icon.attachToNode(n.getId());
            icon.addAttribute("ui.style", "size: 30px; fill-image: url('" + LocalServer.url("/icons/" + getIcon(service) + ".png") + "') ;");

            positioner.add(service, n);
        });

        positioner.compute();

        //provider
        landscape.getItems().forEach(service -> service.getProvidedBy().forEach(providedBy -> {
            Edge e = graph.addEdge(
                    providedBy.getIdentifier() + service.getIdentifier(),
                    providedBy.getIdentifier(),
                    service.getIdentifier()
            );
            e.addAttribute("ui.class", "provides");
            e.addAttribute("ui.style", "text-background-color: #" + Color.nameToRGB(service.getGroup()) + "; ");
            e.addAttribute("layout.weight", 0.5);
        }));

        //dataflow
        landscape.getItems().forEach(service -> service.getRelations().forEach(df -> {

            if (df.getSource().equals(df.getTarget()))
                return;

            String id = "df_" + service.getIdentifier() + df.getTarget();
            Item target = (Item) df.getTarget();
            logger.info("Adding dataflow " + id);
            Edge e = graph.addEdge(
                    id,
                    service.getIdentifier(),
                    target.getIdentifier(),
                    true //directed
            );

            e.addAttribute("ui.class", "dataflow");
            String color = Color.nameToRGB(service.getGroup());
            e.addAttribute("ui.style", "fill-color: #" + color + "; text-background-color: #" + Color.nameToRGB(service.getGroup(), Color.DARK) + "; ");
            e.addAttribute("ui.label", df.getFormat());
        }));

        /*
         * interfaces
         *
         *
         */
        landscape.getItems().forEach(this::addInterfaces);

        /*
         * statuses
         */
        landscape.getItems().forEach(this::addStatuses);


        return graph;

    }

    @Override
    public void render(LandscapeImpl landscape, File file) throws IOException {

        graph = render(landscape);

        String prefix = "prefix";
        FileSinkImages.OutputType type = FileSinkImages.OutputType.PNG;
        FileSinkImages.Resolution resolution = FileSinkImages.Resolutions.HD1080;
        FileSinkImages.OutputPolicy outputPolicy = FileSinkImages.OutputPolicy.BY_STEP;

        FileSinkImages fsi = new FileSinkImages(
                prefix, type, resolution, outputPolicy);
        fsi.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        fsi.setQuality(FileSinkImages.Quality.HIGH);
        fsi.setRenderer(FileSinkImages.RendererType.SCALA);

        fsi.writeAll(graph, file.getAbsolutePath());
    }

    private void addInterfaces(Item item) {

        if (item.getInterfaces().size() == 0)
            return;
        Node serviceNode = graph.getNode(item.getIdentifier());


        AtomicInteger i = new AtomicInteger(1);
        item.getInterfaces().forEach(inter -> {

            String intfID = "interface_" + item.getIdentifier() + i.getAndIncrement();

            Sprite sprite = spriteManager.addSprite(intfID);
            sprite.attachToNode(serviceNode.getId());
            int rotation = 45;
            int offset = -90 - (rotation / 2) * (item.getInterfaces().size() - 1);
            int z = offset + i.get() * rotation;
            sprite.setPosition(StyleConstants.Units.GU, 0.13, 2, z);
            sprite.setAttribute("ui.label", " " + inter.getDescription());
            sprite.setAttribute("ui.class", "interface");
            sprite.setAttribute("ui.style", "fill-color: #" + Color.nameToRGB(item.getGroup()) + "; ");
        });
    }

    private String getStatusColor(Item item) {
        var ref = new Object() {
            Status current = Status.UNKNOWN;
        };

        item.getStatuses().forEach(statusItem -> {
            if (statusItem.getStatus().isHigherThan(ref.current))
                ref.current = statusItem.getStatus();
        });

        return ref.current.toString();
    }

    private void addStatuses(LandscapeItem item) {
        List<StatusItem> displayed = item.getStatuses().stream()
                .filter(statusItem -> !GREEN.equals(statusItem.getStatus()))
                .collect(Collectors.toList());

        if (displayed.size() == 0)
            return;

        Node serviceNode = graph.getNode(item.getIdentifier());

        AtomicInteger i = new AtomicInteger(1);
        displayed.forEach(value -> {

            String statusID = "status_" + value.getLabel() + "_" + serviceNode.getId();

            Sprite sprite = spriteManager.addSprite(statusID);
            sprite.attachToNode(serviceNode.getId());
            int rotation = 45;
            int offset = 90 - (rotation / 2) * (displayed.size() - 1);
            int z = offset + i.getAndIncrement() * rotation;
            sprite.setPosition(StyleConstants.Units.GU, 0.1, 2, z);
            sprite.setAttribute("ui.label", value.getLabel().toUpperCase().substring(0, 3));
            sprite.setAttribute("ui.style", "stroke-mode: plain; " +
                            "fill-color: " + value.getStatus().toString() + "; fill-mode: plain; " +
                            "shape: rounded-box; " +
                            "z-index: 2; " //+
                    //"text-alignment: at-left; "
            );
        });
    }

    private String getIcon(Item item) {
        if (StringUtils.isEmpty(item.getType()))
            return "service";

        //fallback to service
        if (!Arrays.asList(KNOWN_ICONS).contains(item.getType().toLowerCase()))
            return "service";

        return item.getType().toLowerCase();
    }

    public String getGraphDump() {

        StringBuilder s = new StringBuilder(graph.getId() + " ");
        graph.getNodeIterator().forEachRemaining(node -> {
            String n = "NODE " + node.getId() + " " + node.getAttribute("ui.style") + "\n";
            s.append(n);
        });

        graph.getEdgeIterator().forEachRemaining(edge -> {
            String n = "EDGE " + edge.getId() + " " + edge.getAttribute("ui.style") + "\n";
            s.append(n);
        });

        return s.toString();
    }
}
