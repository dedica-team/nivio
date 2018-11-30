package de.bonndan.nivio.output.graphstream;

import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.Service;
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
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.bonndan.nivio.landscape.LandscapeItem.STATUS_GREEN;

public class GraphStreamRenderer implements Renderer {

    private Logger logger = LoggerFactory.getLogger(GraphStreamRenderer.class);

    private Graph graph;
    private SpriteManager spriteManager;

    @Override
    public String render(Landscape landscape) {
        return null;
    }

    @Override
    public void render(Landscape landscape, File file) throws IOException {

        graph = new SingleGraph(landscape.getName());
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", getStylesheet());
        graph.addAttribute("layout.quality", 4);

        spriteManager = new SpriteManager(graph);

        Positioner positioner = new Positioner();

        landscape.getServices().forEach(service -> {
            Node n = graph.addNode(service.getIdentifier());
            n.addAttribute("ui.label", StringUtils.isEmpty(service.getName()) ? service.getIdentifier() : service.getName());
            n.addAttribute("ui.class", service.getLayer());
            n.addAttribute("ui.style", "fill-color: #" + Color.intToARGB(service.getGroup()) + "; ");

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
            e.addAttribute("ui.style", "text-background-color: #" + Color.intToARGB(service.getGroup()) + "; ");
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
            String color = Color.intToARGB(service.getGroup());
            e.addAttribute("ui.style", "fill-color: #" + color + "; text-background-color: #" + Color.intToARGB(service.getGroup(), Color.DARK) + "; ");
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
            int offset = -90 - (rotation / 2) * (service.getInterfaces().size() -1);
            int z = offset + i.get() * rotation;
            sprite.setPosition(StyleConstants.Units.GU, 0.13, 2, z);
            sprite.setAttribute("ui.label", "   "+ inter.getDescription());
            sprite.setAttribute("ui.style", "stroke-mode: plain; " +
                    "fill-color: #" + Color.intToARGB(service.getGroup())+ "; fill-mode: plain; " +
                    "fill-image: url('http://localhost:8080/icons/interface.png'); " +
                    "shape: circle; " +
                    "z-index: 1; ");
        });
    }

    private void addStatuses(Service service) {
        Map<String, String> displayed = service.getStatuses().entrySet().stream()
                .filter(entry -> !STATUS_GREEN.equals(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (displayed.size() == 0)
            return;

        Node serviceNode = graph.getNode(service.getIdentifier());


        AtomicInteger i = new AtomicInteger(1);
        displayed.forEach((key, value) -> {

            String statusID = "status_" + key + "_" + serviceNode.getId();

            Sprite sprite = spriteManager.addSprite(statusID);
            sprite.attachToNode(serviceNode.getId());
            int rotation = 45;
            int offset = 90 - (rotation / 2) * (displayed.size() - 1);
            int z = offset + i.get() * rotation;
            sprite.setPosition(StyleConstants.Units.GU, 0.13, 2, z);
            sprite.setAttribute("ui.label", "   " + key);
            sprite.setAttribute("ui.style", "stroke-mode: plain; " +
                    "fill-color: "+ value + "; fill-mode: plain; " +
                    "shape: circle; " +
                    "z-index: 1; ");
        });
    }

    private String getStylesheet() {
        return
                "graph { padding: 50px;} " +
                        "node {" +
                        "text-padding: 3px; " +
                        "text-alignment: under; " +
                        "text-offset: 0px, 5px; " +
                        "text-size: 12px; " +
                        "stroke-mode: plain; " +
                        "}" +
                        "node.infrastructure { " +
                        "size: 50px; " +
                        "text-background-mode: rounded-box; " +
                        "text-background-color: #333333; " +
                        "text-color: white; " +
                        "}" +
                        "node.ingress { " +
                        "size: 50px; " +
                        "text-background-mode: rounded-box; " +
                        "text-background-color: #333333; " +
                        "text-color: white; " +
                        "}" +
                        "node.applications { " +
                        "size: 50px; " +
                        "shape: rounded-box; " +
                        "text-background-mode: rounded-box; " +
                        "text-background-color: #333333; " +
                        "text-alignment: under; " +
                        "text-color: white; " +
                        "size-mode: fit; " +
                        "}" +
                        "node.interface { " +
                        //"shape: rounded-box; " +
                        "size: 20px; " +
                        "text-padding: 2px; " +
                        //"shape: freeplane; " +
                        "size-mode: fit; " +
                        "fill-image: url('http://localhost:8080/icons/interface.png'); " +
                        "}" +
                        //////////////
                        "edge {  }" +
                        "edge.dataflow { " +
                        "shape: cubic-curve; " +
                        "stroke-color: grey; " +
                        "stroke-width: 1px; " +
                        "stroke-mode: plain; " +
                        "arrow-size: 20px, 4px; " +
                        "size-mode: fit; " +
                        "text-background-mode: rounded-box; " +
                        "text-padding: 5px; " +
                        "text-color: white; " +
                        "text-size: 12px; " +
                        "fill-mode: plain; " +
                        "}" +
                        "edge.provides { " +
                        "stroke-width: 1px; stroke-mode: dashes; " +
                        "size-mode: fit; " +
                        "text-background-mode: rounded-box; " +
                        "text-size: 12px; " +
                        "}" +
                        ///////////
                        "sprite { " +
                        "size: 25px; " +
                        "shape: box; " +
                        "fill-mode: image-scaled-ratio-max; " +
                        "text-alignment: at-right; " +
                        "text-padding: 30px, 0px; " +
                        "}\n";
    }

    private String getIcon(Service service) {
        if (StringUtils.isEmpty(service.getType()))
            return "service";
        return service.getType().toLowerCase();
    }
}
