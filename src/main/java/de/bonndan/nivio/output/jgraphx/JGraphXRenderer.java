package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.output.RenderedArtifact;
import de.bonndan.nivio.output.Renderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JGraphXRenderer implements Renderer<RenderedArtifact<mxGraph, mxCell>> {

    private boolean debugMode;

    public JGraphXRenderer() {
    }

    @Override
    public RenderedArtifact<mxGraph, mxCell> render(LandscapeImpl landscape) {

        Map<String, GroupGraph> subgraphs = new LinkedHashMap<>();
        landscape.getGroups().forEach((name, groupItem) ->  {
            GroupGraph groupGraph = new GroupGraph(((Group)groupItem).getItems());
            subgraphs.put(name, groupGraph);
        });

        Map<String, Group> groupMap = new HashMap<>();
        landscape.getGroups().forEach((s, groupItem) -> groupMap.put(s, (Group)groupItem));
        AllGroupsGraph allGroupsGraph = new AllGroupsGraph(landscape.getConfig(), groupMap, subgraphs);

        if (isDebugMode())
            return allGroupsGraph;

        FinalGraph finalGraph = new FinalGraph();
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
