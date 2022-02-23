package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.output.map.hex.GroupAreaFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexMap;
import de.bonndan.nivio.output.map.hex.MapTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static de.bonndan.nivio.output.map.svg.SVGDocument.DATA_IDENTIFIER;
import static de.bonndan.nivio.output.map.svg.SVGDocument.VISUAL_FOCUS_UNSELECTED;
import static org.assertj.core.api.Assertions.assertThat;

class SVGGroupAreaTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
    }

    @Test
    void hasFQI() {
        MapTile e1 = new MapTile(new Hex(1, 1));
        MapTile e2 = new MapTile(new Hex(3, 3));

        HexMap hexMap = new HexMap();
        hexMap.add(graph.itemAA, e1);
        hexMap.add(graph.itemAB, e2);

        //when
        Set<MapTile> area = GroupAreaFactory.getGroup(hexMap, graph.groupA, Set.of(graph.itemAA, graph.itemAB));
        SVGGroupArea svgGroupArea = SVGGroupArea.forGroup(graph.groupA, area, false);

        assertThat(svgGroupArea.render().render()).contains(graph.groupA.getFullyQualifiedIdentifier().toString());
    }

    @Test
    void supportsVisualFocus() {
        MapTile e1 = new MapTile(new Hex(1, 1));
        MapTile e2 = new MapTile(new Hex(3, 3));

        HexMap hexMap = new HexMap();
        hexMap.add(graph.itemAA, e1);
        hexMap.add(graph.itemAB, e2);

        Set<MapTile> area = GroupAreaFactory.getGroup(hexMap, graph.groupA, Set.of(graph.itemAA, graph.itemAB));
        SVGGroupArea svgGroupArea = SVGGroupArea.forGroup(graph.groupA, area, false);

        //then
        String render1 = svgGroupArea.render().render();
        assertThat(render1).contains(DATA_IDENTIFIER).contains(VISUAL_FOCUS_UNSELECTED);
    }


}