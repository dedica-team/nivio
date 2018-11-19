package de.bonndan.nivio.output.graph;

import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.output.Renderer;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

public class GraphStreamRenderer implements Renderer {

    @Override
    public String render(Landscape landscape) {
        return null;
    }

    @Override
    public void render(Landscape landscape, File file) throws IOException {

        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        Graph graph = new SingleGraph(landscape.getName());
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", getStylesheet());


        landscape.getServices().forEach(service -> {
            Node n = graph.addNode(service.getIdentifier());
            n.addAttribute("ui.label", StringUtils.isEmpty(service.getName()) ? service.getIdentifier() : service.getName());
            n.addAttribute("ui.class", service.getType());
            //  node.addAttribute("ui.style", "fill-color: rgb(0,100,255);");
        });

        //provider
        landscape.getServices().forEach(service -> service.getProvidedBy().forEach(providedBy -> {
            Edge e = graph.addEdge(
                    providedBy.getIdentifier() + service.getIdentifier(),
                    providedBy.getIdentifier(),
                    service.getIdentifier()
            );
            e.addAttribute("ui.class", "provides");
        }));

        //dataflow
        landscape.getServices().forEach(service -> service.getDataFlow().forEach(df -> {
            Edge e = graph.addEdge(
                    "df_" + service.getIdentifier()+df.getTarget(),
                    service.getIdentifier(),
                    df.getTarget()
            );
            e.addAttribute("ui.class", "dataflow");
        }));

        String prefix = "prefix";
        FileSinkImages.OutputType type = FileSinkImages.OutputType.PNG;
        FileSinkImages.Resolution resolution = FileSinkImages.Resolutions.HD720;
        FileSinkImages.OutputPolicy outputPolicy = FileSinkImages.OutputPolicy.BY_STEP;

        FileSinkImages fsi = new FileSinkImages(
                prefix, type, resolution, outputPolicy);
        fsi.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        fsi.setQuality(FileSinkImages.Quality.HIGH);
        fsi.setRenderer(FileSinkImages.RendererType.SCALA);

        fsi.writeAll(graph, file.getAbsolutePath());
    }

    private String getStylesheet() {
        return
        "graph { padding: 50px; }" +
        "node { fill-color: #3d5689; size-mode: fit; text-background-mode: rounded-box; text-background-color: orange; }" +
        "edge {  }" +
        "edge.dataflow { shape: cubic-curve; stroke-color: blue; stroke-width: 1px; stroke-mode: plain; arrow-size: 20px, 4px; }" +
        "edge.provides { stroke-width: 1px; stroke-mode: dashes; }\n";
    }
}
