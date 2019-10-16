package de.bonndan.nivio.output.jgraphx;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.input.EnvironmentFactory;
import de.bonndan.nivio.input.Indexer;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.notification.NotificationService;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JGraphXRendererTest {

    private LandscapeRepository landscapeRepository;
    private Indexer indexer;

    @BeforeEach
    public void setup() {
        landscapeRepository = new LandscapeRepository();
        indexer = new Indexer(landscapeRepository, new NotificationService(null));
    }

    private LandscapeImpl getLandscape(String path) {
        File file = new File(RootPath.get() + path);
        LandscapeDescription landscapeDescription = EnvironmentFactory.fromYaml(file);
        indexer.reIndex(landscapeDescription);
        return landscapeRepository.findDistinctByIdentifier(landscapeDescription.getIdentifier()).orElseThrow();
    }

    private mxGraph debugRender(String path) throws IOException {

        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer(null);
        jGraphXRenderer.setDebugMode(true);

        LandscapeImpl landscape = getLandscape(path + ".yml");
        mxGraph graph = jGraphXRenderer.render(landscape);

        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, null, true, null);
        assertNotNull(image);

        File png = new File(RootPath.get() + path + "_debug.png");
        ImageIO.write(image, "PNG", png);

        return graph;
    }

    @Test
    public void debugRenderExample() throws IOException {
        debugRender("/src/test/resources/example/example_env");
    }

    @Test
    public void debugRenderFourGroups() throws IOException {
        debugRender("/src/test/resources/example/example_four_groups");
    }

    @Test
    public void debugRenderInout() throws IOException {
        debugRender("/src/test/resources/example/inout");
    }
}