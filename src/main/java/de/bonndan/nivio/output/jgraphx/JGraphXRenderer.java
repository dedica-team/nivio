package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.IconService;
import de.bonndan.nivio.output.Rendered;
import de.bonndan.nivio.output.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JGraphXRenderer implements Renderer<Rendered<mxGraph, mxCell>> {

    private static final Logger logger = LoggerFactory.getLogger(JGraphXRenderer.class);
    private final IconService iconService;
    private boolean debugMode;

    public JGraphXRenderer(IconService iconService) {
        this.iconService = iconService;
    }

    @Override
    public Rendered<mxGraph, mxCell> render(LandscapeImpl landscape) {

        Map<String, GroupGraph> subgraphs = new LinkedHashMap<>();
        landscape.getGroups().forEach((name, groupItem) ->  {
            GroupGraph groupGraph = new GroupGraph(((Group)groupItem).getItems());
            subgraphs.put(name, groupGraph);
        });

        Map<String, Group> groupMap = new HashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groupMap.put(s, (Group)groupItem)); //TODO
        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(landscape.getConfig(), groupMap, subgraphs);

        if (isDebugMode())
            return allGroupsGraph;

        FinalGraph finalGraph = new FinalGraph(iconService);
        finalGraph.render(allGroupsGraph, subgraphs);
        return finalGraph;
    }

    @Override
    public void render(LandscapeImpl landscape, File file) throws IOException {

        mxGraph graph = render(landscape).getRendered();
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, true, null);

        ImageIO.write(image, "PNG", file);
    }

    private boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Triggers debug rendering of groups
     */
    void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
