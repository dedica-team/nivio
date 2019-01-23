package de.bonndan.nivio.output.graphstream;

import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.Service;
import de.bonndan.nivio.landscape.Status;
import de.bonndan.nivio.landscape.StatusItem;
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

import static de.bonndan.nivio.landscape.Status.GREEN;


public class GraphStreamRenderer implements Renderer {

    private Logger logger = LoggerFactory.getLogger(GraphStreamRenderer.class);

    private Graph graph;
    private SpriteManager spriteManager;

    private static final String[] KNOWN_ICONS = new String[]{
            "api.png",
            "cache.png",
            "database.png",
            "dataflow.png",
            "firewall.png",
            "humanuser.png",
            "interface.png",
            "keyvaluestore.png",
            "loadbalancer.png",
            "lock.png",
            "messagequeue.png",
            "mobileclient.png",
            "server.png",
            "service.png",
            "webservice.png",
    };

    @Override
    public String render(Landscape landscape) {
        return null;
    }

    @Override
    public void render(Landscape landscape, File file) throws IOException {

        graph = new SingleGraph(landscape.getName());
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('http://localhost:8080/css/graph.css')");
        graph.addAttribute("layout.quality", 4);

        spriteManager = new SpriteManager(graph);

        Positioner positioner = new Positioner();

        landscape.getServices().forEach(service -> {
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
            icon.addAttribute("ui.style", "size: 30px; fill-image: url('http://localhost:8080/icons/" + getIcon(service) + ".png') ;");

            positioner.add(service, n);
        });

        positioner.compute();

        //provider
        landscape.getServices().forEach(service -> service.getProvidedBy().forEach(providedBy -> {
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
        landscape.getServices().forEach(service -> service.getDataFlow().forEach(df -> {

            if (df.getSource().equals(df.getTarget()))
                return;

            String id = "df_" + service.getIdentifier() + df.getTarget();
            logger.info("Adding dataflow " + id);
            Edge e = graph.addEdge(
                    id,
                    service.getIdentifier(),
                    df.getTarget(),
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
        landscape.getServices().forEach(this::addInterfaces);

        /*
         * statuses
         */
        landscape.getServices().forEach(this::addStatuses);


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

    private void addInterfaces(Service service) {

        if (service.getInterfaces().size() == 0)
            return;
        Node serviceNode = graph.getNode(service.getIdentifier());


        AtomicInteger i = new AtomicInteger(1);
        service.getInterfaces().forEach(inter -> {

            String intfID = "interface_" + service.getIdentifier() + i.getAndIncrement();

            Sprite sprite = spriteManager.addSprite(intfID);
            sprite.attachToNode(serviceNode.getId());
            int rotation = 45;
            int offset = -90 - (rotation / 2) * (service.getInterfaces().size() - 1);
            int z = offset + i.get() * rotation;
            sprite.setPosition(StyleConstants.Units.GU, 0.13, 2, z);
            sprite.setAttribute("ui.label", " " + inter.getDescription());
            sprite.setAttribute("ui.class", "interface");
            sprite.setAttribute("ui.style", "fill-color: #" + Color.nameToRGB(service.getGroup()) + "; ");
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

    private void addStatuses(Service service) {
        List<StatusItem> displayed = service.getStatuses().stream()
                .filter(item -> !GREEN.equals(item.getStatus()))
                .collect(Collectors.toList());

        if (displayed.size() == 0)
            return;

        Node serviceNode = graph.getNode(service.getIdentifier());

        AtomicInteger i = new AtomicInteger(1);
        displayed.forEach(value -> {

            String statusID = "status_" + value.getLabel() + "_" + serviceNode.getId();

            Sprite sprite = spriteManager.addSprite(statusID);
            sprite.attachToNode(serviceNode.getId());
            int rotation = 45;
            int offset = 90 - (rotation / 2) * (displayed.size() - 1);
            int z = offset + i.getAndIncrement() * rotation;
            sprite.setPosition(StyleConstants.Units.GU, 0.1, 2, z);
            sprite.setAttribute("ui.label", value.getLabel().toUpperCase().substring(0,3));
            sprite.setAttribute("ui.style", "stroke-mode: plain; " +
                    "fill-color: " + value.getStatus().toString() + "; fill-mode: plain; " +
                    "shape: rounded-box; " +
                    "z-index: 2; " //+
                    //"text-alignment: at-left; "
            );
        });
    }

    private String getIcon(Service service) {
        if (StringUtils.isEmpty(service.getType()))
            return "service";

        //fallback to service
        if (!Arrays.asList(KNOWN_ICONS).contains(service.getType().toLowerCase()))
            return "service";

        return service.getType().toLowerCase();
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
