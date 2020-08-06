package de.bonndan.nivio.output.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.Indexer;
import de.bonndan.nivio.input.ItemDescriptionFormatFactory;
import de.bonndan.nivio.input.LandscapeDescriptionFactory;
import de.bonndan.nivio.input.csv.ItemDescriptionFactoryCSV;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.input.nivio.ItemDescriptionFactoryNivio;
import de.bonndan.nivio.model.LandscapeImpl;
import de.bonndan.nivio.model.LandscapeRepository;
import de.bonndan.nivio.output.LocalServer;
import de.bonndan.nivio.output.icons.VendorIcons;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import de.bonndan.nivio.util.RootPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrganicLayouterTest {

    private LandscapeRepository landscapeRepository;
    private ItemDescriptionFormatFactory formatFactory;
    private Indexer indexer;
    private LandscapeDescriptionFactory factory;

    @BeforeEach
    public void setup() {
        landscapeRepository = new LandscapeRepository();
        formatFactory = ItemDescriptionFormatFactory.with(ItemDescriptionFactoryNivio.forTesting());
        FileFetcher fileFetcher = new FileFetcher(mock(HttpService.class));
        factory = new LandscapeDescriptionFactory(fileFetcher);

        indexer = new Indexer(landscapeRepository, formatFactory, mock(ApplicationEventPublisher.class),new LocalServer("", new VendorIcons()));
    }

    private LandscapeImpl getLandscape(String path) {
        File file = new File(RootPath.get() + path);
        LandscapeDescription landscapeDescription = factory.fromYaml(file);
        indexer.reIndex(landscapeDescription);
        return landscapeRepository.findDistinctByIdentifier(landscapeDescription.getIdentifier()).orElseThrow();
    }

    private LayoutedComponent debugRender(String path) throws IOException {
        return debugRender(path, true);
    }

    private LayoutedComponent debugRender(String path, boolean debugMode) throws IOException {
        LandscapeImpl landscape = getLandscape(path + ".yml");
        return debugRenderLandscape(path, landscape, debugMode);
    }

    private LayoutedComponent debugRenderLandscape(String path, LandscapeImpl landscape, boolean debugMode) throws IOException {

        OrganicLayouter layouter = new OrganicLayouter();
        LayoutedComponent graph = layouter.layout(landscape);
        toSVG(graph, RootPath.get() + path);
        return graph;
    }

    private void toSVG(LayoutedComponent layoutedComponent, String filename) throws IOException {

        MapStyleSheetFactory mapStyleSheetFactory = mock(MapStyleSheetFactory.class);
        when(mapStyleSheetFactory.getMapStylesheet(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("");

        File json = new File(filename + "_debug.json");
        new ObjectMapper().writeValue(json, layoutedComponent);

        SVGRenderer svgRenderer = new SVGRenderer(mapStyleSheetFactory);
        String svg = svgRenderer.render(layoutedComponent);

        File svgFile = new File(filename + "_debug.svg");
        FileWriter fileWriter = new FileWriter(svgFile);
        fileWriter.write(svg);
        fileWriter.close();
    }

    @Test
    public void debugRenderExample() throws IOException {
        debugRender("/src/test/resources/example/example_env");
    }

    @Test
    public void debugRenderInOut() throws IOException {
        debugRender("/src/test/resources/example/inout");
    }

    @Test
    public void debugRenderFourGroups() throws IOException {
        debugRender("/src/test/resources/example/example_four_groups");
    }

    @Test
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

        debugRenderLandscape("/src/test/resources/example/large", landscape, false);
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
    public void renderCSV() throws IOException {

        formatFactory = ItemDescriptionFormatFactory.with(new ItemDescriptionFactoryCSV(new FileFetcher(new HttpService())));
        indexer = new Indexer(landscapeRepository, formatFactory, mock(ApplicationEventPublisher.class), new LocalServer("", new VendorIcons()));

        debugRender("/src/test/resources/example/example_csv", false);
    }
}