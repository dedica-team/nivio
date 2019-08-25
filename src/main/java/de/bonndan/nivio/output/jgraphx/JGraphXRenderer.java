package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import de.bonndan.nivio.landscape.*;
import de.bonndan.nivio.output.IconService;
import de.bonndan.nivio.output.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class JGraphXRenderer implements Renderer<mxGraph> {

    public final int DEFAULT_ICON_SIZE = 50;
    private final IconService iconService;

    private Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);
    private Map<Service, mxCell> serviceVertexes = new HashMap<>();
    private mxStylesheet stylesheet;
    private mxGraph graph;
    private Map<String, mxCell> groupNodes = new HashMap<>();
    private Map<String, GroupGraph> subgraphs = new LinkedHashMap<String, GroupGraph>();

    public JGraphXRenderer(IconService iconService) {
        this.iconService = iconService;
    }

    @Override
    public mxGraph render(Landscape landscape) {

        Groups groups = Groups.from(landscape);
        groups.getAll().forEach((groupName, serviceItems) -> {
            GroupGraph groupGraph = new GroupGraph(serviceItems);
            subgraphs.put(groupName, groupGraph);
        });

        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(groups, subgraphs);

        FinalGraph finalGraph = new FinalGraph(iconService);
        return finalGraph.render(allGroupsGraph, subgraphs);
    }

    @Override
    public void render(Landscape landscape, File file) throws IOException {

        graph = render(landscape);
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, true, null);

        ImageIO.write(image, "PNG", file);

    }
}
