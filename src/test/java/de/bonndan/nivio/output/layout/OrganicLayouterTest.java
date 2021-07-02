package de.bonndan.nivio.output.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.input.FileFetcher;
import de.bonndan.nivio.input.Indexer;
import de.bonndan.nivio.input.InputFormatHandlerFactory;
import de.bonndan.nivio.input.external.LinkHandlerFactory;
import de.bonndan.nivio.input.csv.InputFormatHandlerCSV;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.input.http.HttpService;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.model.LandscapeFactory;
import de.bonndan.nivio.output.RenderingTest;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.icons.LocalIcons;
import de.bonndan.nivio.output.icons.ExternalIcons;
import de.bonndan.nivio.output.map.svg.MapStyleSheetFactory;
import de.bonndan.nivio.output.map.svg.SVGDocument;
import de.bonndan.nivio.output.map.svg.SVGRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrganicLayouterTest extends RenderingTest {

    @BeforeEach
    public void setup() throws URISyntaxException {
        super.setup();
    }

    private LayoutedComponent debugRender(String path) throws IOException {
        return debugRender(path, true);
    }

    private LayoutedComponent debugRender(String path, boolean debugMode) throws IOException {
        Landscape landscape = getLandscape(path + ".yml");
        return debugRenderLandscape(path, landscape);
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
        Landscape landscape = getLandscape(path + ".yml");
        debugRenderLandscape(path, landscape);
    }

    @Test
    public void debugRenderLargeGraph() throws IOException {

        LandscapeDescription input = new LandscapeDescription("largetest", "largetest", null);

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

        indexer.index(input);
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(input.getIdentifier()).orElseThrow();

        debugRenderLandscape("/src/test/resources/example/large", landscape);
    }

    @Test
    public void debugRenderLargeGraphSVG() throws IOException {

        LandscapeDescription input = new LandscapeDescription("largetest", "largetest", null);

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

        indexer.index(input);
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(input.getIdentifier()).orElseThrow();

        debugRenderLandscape("/src/test/resources/example/large", landscape);
    }

    @Test
    public void renderLandscapeItemModelWithMagicLabels() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ItemDescription model = new ItemDescription();
        model.setIdentifier("item");
        model.setName("Item Description");

        Map<String, Object> map = mapper.convertValue(model, Map.class);

        LandscapeDescription landscapeDescription = new LandscapeDescription("landscapeItem:model", "Landscape Item Model", null);
        landscapeDescription.getItemDescriptions().add(model);

        map.forEach((field, o) -> {
            ItemDescription d = new ItemDescription();
            d.setIdentifier(field);
            landscapeDescription.getItemDescriptions().add(d);
            model.getLabels().put(field + "_PROVIDER_URL", field.toLowerCase());
        });

        indexer.index(landscapeDescription);
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeDescription.getIdentifier()).orElseThrow();

        debugRenderLandscape("/src/test/resources/example/model", landscape);
    }

    @Test
    public void renderCSV() throws IOException {

        HttpService httpService = new HttpService();
        IconService iconService = new IconService(new LocalIcons(), new ExternalIcons(httpService));
        formatFactory = new InputFormatHandlerFactory(List.of(new InputFormatHandlerCSV(new FileFetcher(httpService))));
        LinkHandlerFactory linkHandlerFactory = mock(LinkHandlerFactory.class);
        indexer = new Indexer(landscapeRepository, formatFactory, linkHandlerFactory, mock(ApplicationEventPublisher.class),  iconService);

        debugRender("/src/test/resources/example/example_csv", false);
    }

    @Test
    public void shiftGroupsAndItems() {

        //given
        String path = "/src/test/resources/example/inout";
        Landscape landscape = getLandscape(path + ".yml");


        //when
        OrganicLayouter layouter = new OrganicLayouter();
        LayoutedComponent lc = layouter.layout(landscape);

        LayoutedComponent itemComponent = lc.getChildren().get(0).getChildren().get(0);
        assertNotNull(itemComponent);

        //check items are shifted
        assertEquals(691.4090488048637, itemComponent.getX()); //margin + group offset + own offset
        assertEquals(1459.8723644530933, itemComponent.getY()); //margin + group offset + own offset
    }
}