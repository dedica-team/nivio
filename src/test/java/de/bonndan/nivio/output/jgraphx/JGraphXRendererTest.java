package de.bonndan.nivio.output.jgraphx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import de.bonndan.nivio.input.*;
import de.bonndan.nivio.input.csv.ItemDescriptionFactoryCSV;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import de.bonndan.nivio.model.LandscapeConfig;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.notification.NotificationService;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.RenderedArtifact;
import de.bonndan.nivio.output.icons.VendorIcons;
import de.bonndan.nivio.output.map.MapFactory;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SvgFactory;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JGraphXRendererTest {

    private LandscapeRepository landscapeRepository;
    private ItemDescriptionFormatFactory formatFactory;
    private Indexer indexer;

    @BeforeEach
    public void setup() {
        landscapeRepository = new LandscapeRepository();
        formatFactory = ItemDescriptionFormatFactory.with(ItemDescriptionFactoryNivio.forTesting());

        indexer = new Indexer(landscapeRepository, formatFactory, new NotificationService(null));
    }

    private LandscapeImpl getLandscape(String path) {
        File file = new File(RootPath.get() + path);
        LandscapeDescription landscapeDescription = LandscapeDescriptionFactory.fromYaml(file);
        indexer.reIndex(landscapeDescription);
        return landscapeRepository.findDistinctByIdentifier(landscapeDescription.getIdentifier()).orElseThrow();
    }

    private mxGraph debugRender(String path) throws IOException {
        return debugRender(path, true);
    }

    private mxGraph debugRender(String path, boolean debugMode) throws IOException {
        LandscapeImpl landscape = getLandscape(path + ".yml");
        return debugRenderLandscape(path, landscape, debugMode);
    }

    private mxGraph debugRenderLandscape(String path, LandscapeImpl landscape, boolean debugMode) throws IOException {

        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer();
        jGraphXRenderer.setDebugMode(debugMode);
        mxGraph graph = jGraphXRenderer.render(landscape).getRendered();

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
    @Disabled("Requires network connection without debug mode")
    public void renderInout() throws IOException {
        String path = "/src/test/resources/example/inout";
        LandscapeImpl landscape = getLandscape(path + ".yml");
        debugRenderLandscape(path, landscape, false);
    }

    @Test
    public void debugRenderLargeGraph() throws IOException {

        LandscapeDescription input = new LandscapeDescription();
        input.setIdentifier("largetest");
        input.setName("largetest");

        int g = 0;
        while (g < 30) {

            int i = 0;
            int max = g % 2 > 0 ? 5 : 8;
            GroupDescription gd = new GroupDescription();
            String groupIdentifier = "group" + g;
            gd.setIdentifier(groupIdentifier);
            input.getGroups().put(groupIdentifier, gd);
            while (i < max) {
                ItemDescription itemDescription = new ItemDescription();
                itemDescription.setIdentifier(groupIdentifier + "_item_" + i);
                itemDescription.setGroup(groupIdentifier);
                input.getItemDescriptions().add(itemDescription);
                i++;
            }
            g++;
        }

        indexer.reIndex(input);
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(input.getIdentifier()).orElseThrow();

        debugRenderLandscape("/src/test/resources/example/large", landscape, false);
    }

    @Test
    @Disabled
    public void debugRenderLargeGraphSVG() throws IOException {

        LandscapeDescription input = new LandscapeDescription();
        input.setIdentifier("largetest");
        input.setName("largetest");

        List<ItemDescription> descriptionList = new ArrayList<>();
        int g = 0;
        while (g < 30) {

            int i = 0;
            int max = g % 2 > 0 ? 5 : 8;
            GroupDescription gd = new GroupDescription();
            String groupIdentifier = "group" + g;
            gd.setIdentifier(groupIdentifier);
            input.getGroups().put(groupIdentifier, gd);
            while (i < max) {
                ItemDescription itemDescription = new ItemDescription();
                itemDescription.setIdentifier(groupIdentifier + "_item_" + i);
                itemDescription.setGroup(groupIdentifier);
                input.getItemDescriptions().add(itemDescription);
                descriptionList.add(itemDescription);
                i++;
            }
            g++;
        }

        for (int i = 0; i < 20; i++) {
            var source = descriptionList.get(i);
            var target = descriptionList.get(i + 20);
            source.addRelation(new RelationDescription(source.getIdentifier(), target.getIdentifier()));
        }

        indexer.reIndex(input);
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(input.getIdentifier()).orElseThrow();

        JGraphXRenderer jGraphXRenderer = new JGraphXRenderer();
        MapFactory<mxGraph, mxCell> mapFactory = new RenderedXYMapFactory(new LocalServer("", new VendorIcons()));
        RenderedArtifact<mxGraph, mxCell> render = jGraphXRenderer.render(landscape);
        mapFactory.applyArtifactValues(landscape, render);

        MapStyleSheetFactory mapStyleSheetFactory = mock(MapStyleSheetFactory.class);
        when(mapStyleSheetFactory.getMapStylesheet(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("");
        SvgFactory svgFactory = new SvgFactory(landscape, mapStyleSheetFactory);
        svgFactory.setDebug(true);
        String svg = svgFactory.getXML();

        File png = new File(RootPath.get() + "/src/test/resources/example/large" + ".svg");
        FileWriter fileWriter = new FileWriter(png);
        fileWriter.write(svg);
        fileWriter.close();
    }

    @Test
    public void renderLandscapeItemModelWithMagicLabels() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ItemDescription model = new ItemDescription();
        model.setIdentifier("item");
        model.setName("Item Description");

        Map<String, Object> map = mapper.convertValue(model, Map.class);

        LandscapeDescription landscapeDescription = new LandscapeDescription();
        landscapeDescription.setIdentifier("landscapeItem:model");
        landscapeDescription.setName("Landscape Item Model");
        landscapeDescription.getItemDescriptions().add(model);

        map.forEach((field, o) -> {
            ItemDescription d = new ItemDescription();
            d.setIdentifier(field);
            landscapeDescription.getItemDescriptions().add(d);
            model.getLabels().put(field + "_PROVIDER_URL", field.toLowerCase());
        });

        indexer.reIndex(landscapeDescription);
        LandscapeImpl landscape = landscapeRepository.findDistinctByIdentifier(landscapeDescription.getIdentifier()).orElseThrow();

        debugRenderLandscape("/src/test/resources/example/model", landscape, false);
    }

    @Test
    @Disabled("Requires network connection without debug mode")
    public void renderCSV() throws IOException {

        formatFactory = ItemDescriptionFormatFactory.with(new ItemDescriptionFactoryCSV(new FileFetcher(new HttpService())));
        indexer = new Indexer(landscapeRepository, formatFactory, new NotificationService(null));

        debugRender("/src/test/resources/example/example_csv", false);
    }
}