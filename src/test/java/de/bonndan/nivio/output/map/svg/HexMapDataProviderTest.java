package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.Landscape;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.hex.HexMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HexMapDataProviderTest {

    private GraphTestSupport graph;
    private LayoutedComponent layouted;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
        layouted = getLayoutedLandscape(graph.landscape);
    }

    @Test
    void testContainsGroupAndItems() {

        //given
        HexMap hexMap = new HexMap();

        //when
        HexMapDataProvider.fillMap(hexMap, layouted);

        //then
        assertThat(hexMap.getTileForItem(graph.itemAA)).isNotNull();
        assertThat(hexMap.getTileForItem(graph.itemAB)).isNotNull();
        assertThat(hexMap.getTileForItem(graph.itemAC)).isNotNull();
        assertThat(hexMap.getGroupArea(graph.groupA)).isNotNull();
    }

    private LayoutedComponent getLayoutedLandscape(Landscape landscape) {

        LayoutedComponent itemLayout1 = new LayoutedComponent(graph.itemAA, List.of(), Collections.emptyList(), 100, 100);
        itemLayout1.setCenterX(0);
        itemLayout1.setCenterY(0);

        LayoutedComponent itemLayout2 = new LayoutedComponent(graph.itemAB, List.of(), Collections.emptyList(), 100, 100);
        itemLayout2.setCenterX(0);
        itemLayout2.setCenterY(400);

        LayoutedComponent itemLayout3 = new LayoutedComponent(graph.itemAC, List.of(), Collections.emptyList(), 100, 100);
        itemLayout3.setCenterX(400);
        itemLayout3.setCenterY(400);


        LayoutedComponent groupLayout = new LayoutedComponent(graph.groupA, List.of(itemLayout1, itemLayout2, itemLayout3), Collections.emptyList(), 100, 100);
        groupLayout.setCenterX(50);
        groupLayout.setCenterY(66);

        return LayoutedComponent.from(landscape, List.of(groupLayout));
    }
}