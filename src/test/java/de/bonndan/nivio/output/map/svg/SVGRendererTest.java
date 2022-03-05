package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.assessment.Assessment;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.GroupBuilder;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.icons.IconService;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SVGRendererTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
    }

    @Test
    void testRendering() {

        //given

        IconService iconService = mock(IconService.class);
        when(iconService.getIconUrl(any(Item.class))).thenReturn("https://foo.bar/icon.png");
        MapStyleSheetFactory mapStyleSheetFactory = mock(MapStyleSheetFactory.class);
        SVGRenderer svgRenderer = new SVGRenderer(mapStyleSheetFactory);

        LayoutedComponent lc = getLayoutedLandscape(graph.landscape);

        //when
        String rendered = svgRenderer.render(lc, Assessment.empty(), true);

        //check svg xml is returned
        assertTrue(rendered.contains("svg version=\"1.1\""));
    }

    private LayoutedComponent getLayoutedLandscape(Landscape landscape) {

        Group group = GroupBuilder.aGroup().withIdentifier("bar").withName("landscapeIdentifier").withParent(graph.context).build();
        landscape.getWriteAccess().addOrReplaceChild(group);


        Item baz = graph.getTestItem("bar", "baz");
        LayoutedComponent itemLayout = new LayoutedComponent(baz, List.of(), Collections.emptyList(), 100, 100);
        itemLayout.setCenterX(50);
        itemLayout.setCenterY(66);


        LayoutedComponent groupLayout = new LayoutedComponent(group, List.of(itemLayout), Collections.emptyList(), 100, 100);
        groupLayout.setCenterX(50);
        groupLayout.setCenterY(66);


        return LayoutedComponent.from(landscape, List.of(groupLayout));
    }
}