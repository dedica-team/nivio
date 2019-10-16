package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.Groups;
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

    private final IconService iconService;

    private Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);
    private Map<String, GroupGraph> subgraphs = new LinkedHashMap<>();

    public JGraphXRenderer(IconService iconService) {
        this.iconService = iconService;
    }

    @Override
    public mxGraph render(LandscapeImpl landscape) {

        Groups groups = Groups.from(landscape);
        groups.getAll().forEach((groupName, serviceItems) -> {
            GroupGraph groupGraph = new GroupGraph(serviceItems);
            subgraphs.put(groupName, groupGraph);
        });

        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(landscape.getConfig(), groups, subgraphs);

        return allGroupsGraph.getGraph();

        //FinalGraph finalGraph = new FinalGraph(iconService);
        //return finalGraph.render(allGroupsGraph, subgraphs);
    }

    @Override
    public void render(LandscapeImpl landscape, File file) throws IOException {

        mxGraph graph = render(landscape);
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, true, null);

        ImageIO.write(image, "PNG", file);

    }
}
