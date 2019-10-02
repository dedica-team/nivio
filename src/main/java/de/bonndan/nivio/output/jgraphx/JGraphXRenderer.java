package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.Groups;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeImpl;
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
    public mxGraph render(LandscapeImpl landscape) {

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
    public void render(LandscapeImpl landscape, File file) throws IOException {

        mxGraph graph = render(landscape);
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, true, null);

        ImageIO.write(image, "PNG", file);

    }

    public mxGraph getFinalGraph(LandscapeImpl landscape, FinalGraph finalGraph) {
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
