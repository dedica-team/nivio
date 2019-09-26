package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.landscape.Groups;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.output.IconService;
import de.bonndan.nivio.output.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JGraphXRenderer implements Renderer<mxGraph> {

    private static final Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);
    private final IconService iconService;


    public JGraphXRenderer(IconService iconService) {
        this.iconService = iconService;
    }

    @Override
    public mxGraph render(Landscape landscape) {

        Map<String, GroupGraph> subgraphs = new LinkedHashMap<>();
        Groups groups = Groups.from(landscape);
        groups.getAll().forEach((groupName, serviceItems) -> {
            GroupGraph groupGraph = new GroupGraph(serviceItems);
            subgraphs.put(groupName, groupGraph);
        });

        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(landscape.getConfig(), groups, subgraphs);

        //return allGroupsGraph.getGraph();

        FinalGraph finalGraph = new FinalGraph(iconService);
        return getFinalGraph(landscape, finalGraph);
    }

    @Override
    public void render(Landscape landscape, File file) throws IOException {

        mxGraph graph = render(landscape);
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, true, null);

        ImageIO.write(image, "PNG", file);

    }

    public mxGraph getFinalGraph(Landscape landscape, FinalGraph finalGraph) {
        Map<String, GroupGraph> subgraphs = new LinkedHashMap<>();
        Groups groups = Groups.from(landscape);
        groups.getAll().forEach((groupName, serviceItems) -> {
            GroupGraph groupGraph = new GroupGraph(serviceItems);
            subgraphs.put(groupName, groupGraph);
        });

        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(landscape.getConfig(), groups, subgraphs);

        //return allGroupsGraph.getGraph();

        return finalGraph.render(allGroupsGraph, subgraphs);
    }
}
