package de.bonndan.nivio.output.layout;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bonndan.nivio.input.dto.GroupDescription;
import de.bonndan.nivio.input.dto.ItemDescription;
import de.bonndan.nivio.input.dto.LandscapeDescription;
import de.bonndan.nivio.input.dto.RelationDescription;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.RenderingTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrganicLayouterTest extends RenderingTest {

    @BeforeEach
    public void setup() throws URISyntaxException {
        super.setup();
    }

    private LayoutedComponent debugRender(String path) throws IOException {
        Landscape landscape = getLandscape(path + ".yml");
        return debugRenderLandscape(path, landscape);
    }

    @Test
    void debugRenderExample() throws IOException {
        debugRender("/src/test/resources/example/example_env");
    }

    @Test
    void debugRenderInOut() throws IOException {
        debugRender("/src/test/resources/example/inout");
    }

    @Test
    void debugRenderFourGroups() throws IOException {
        debugRender("/src/test/resources/example/example_four_groups");
    }

    @Test
    void debugRenderDedica() throws IOException {
        debugRender("/src/test/resources/example/dedica");
    }

    @Test
    void debugRenderInternals() throws IOException {
        debugRender("/src/test/resources/example/internals");
    }

    @Test
    void debugRenderPetClinic() throws IOException {
        debugRender("/src/test/resources/example/pet_clinic");
    }

    @Test
    void renderInout() throws IOException {
        String path = "/src/test/resources/example/inout";
        Landscape landscape = getLandscape(path + ".yml");
        debugRenderLandscape(path, landscape);
    }

    // run this test manually, it creates a really large map
    @Disabled
    @Test
    void debugRenderLargeGraph() throws IOException {

        LandscapeDescription input = new LandscapeDescription("largetest", "largetest", null);

        int g = 0;
        List<ItemDescription> descriptionList = new ArrayList<>();
        while (g < 10) {

            int i = 0;
            int max = g % 2 > 0 ? 10 : 40;
            GroupDescription gd = new GroupDescription();
            String groupIdentifier = "group" + g;
            gd.setIdentifier(groupIdentifier);
            input.getWriteAccess().addOrReplaceChild(gd);
            while (i < max) {
                ItemDescription itemDescription = new ItemDescription();
                itemDescription.setIdentifier(groupIdentifier + "_item_" + i);
                itemDescription.setGroup(groupIdentifier);
                input.getWriteAccess().addOrReplaceChild(itemDescription);
                descriptionList.add(itemDescription);
                i++;
            }
            g++;
        }

        Random r = new Random();
        int low = 0;
        int high = descriptionList.size() - 1;

        // relations
        for (int i = 0, descriptionListSize = descriptionList.size(); i < descriptionListSize; i++) {
            ItemDescription itemDescription = descriptionList.get(i);
            List<ItemDescription> targets = new ArrayList<>();
            int j = 0;
            while (j < 5) {
                int rand = r.nextInt(high - low) + low;
                if (rand == i)
                    continue;
                targets.add(descriptionList.get(j));
                j++;
            }
            targets.forEach(target -> {
                RelationDescription rel = new RelationDescription(itemDescription.getFullyQualifiedIdentifier().toString(), target.getFullyQualifiedIdentifier().toString());
                itemDescription.addOrReplaceRelation(rel);
            });
        }

        // index
        indexer.index(input);
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(input.getIdentifier()).orElseThrow();

        debugRenderLandscape("/src/test/resources/example/large", landscape);
    }

    @Test
    void debugRenderLargeGraphSVG() throws IOException {

        LandscapeDescription input = new LandscapeDescription("largetest", "largetest", null);

        List<ItemDescription> descriptionList = new ArrayList<>();
        int g = 0;
        while (g < 30) {

            int i = 0;
            int max = g % 2 > 0 ? 5 : 8;
            GroupDescription gd = new GroupDescription();
            String groupIdentifier = "group" + g;
            gd.setIdentifier(groupIdentifier);
            input.getWriteAccess().addOrReplaceChild(gd);
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
            source.addOrReplaceRelation(new RelationDescription(source.getIdentifier(), target.getIdentifier()));
        }

        indexer.index(input);
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(input.getIdentifier()).orElseThrow();

        debugRenderLandscape("/src/test/resources/example/large", landscape);
    }

    @Test
    void renderLandscapeItemModelWithMagicLabels() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ItemDescription model = new ItemDescription();
        model.setIdentifier("item");
        model.setName("Item Description");

        Map<String, Object> map = mapper.convertValue(model, Map.class);

        LandscapeDescription landscapeDescription = new LandscapeDescription("landscapeItem:model", "Landscape Item Model", null);
        landscapeDescription.getWriteAccess().addOrReplaceChild(model);

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
    void renderCSV() throws IOException {
        debugRender("/src/test/resources/example/example_csv");
    }

    @Test
    void shiftGroupsAndItems() {

        //given
        String path = "/src/test/resources/example/inout";
        Landscape landscape = getLandscape(path + ".yml");


        //when
        OrganicLayouter layouter = new OrganicLayouter();
        LayoutedComponent lc = layouter.layout(landscape);

        LayoutedComponent itemComponent = lc.getChildren().get(0).getChildren().get(0);
        assertNotNull(itemComponent);

        //check items are shifted
        assertEquals(3959, itemComponent.getX()); //margin + group offset + own offset
        assertEquals(2420, itemComponent.getY()); //margin + group offset + own offset
    }
}