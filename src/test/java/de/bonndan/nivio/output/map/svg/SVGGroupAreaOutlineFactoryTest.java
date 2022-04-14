package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.output.map.hex.GroupAreaFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexMap;
import de.bonndan.nivio.output.map.hex.MapTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SVGGroupAreaOutlineFactoryTest {

    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
    }

    @Test
    @DisplayName("Ensure that items far apart have one outline")
    void twoSeparateHexes() {
        Hex e1 = new Hex(0, 100);
        Hex e2 = new Hex(0, 20);

        Group foo = graph.getTestGroup("foo");
        foo.setLabel(Label.color, "005500");

        Item item1 = graph.getTestItem("foo", "bar");
        Item item2 = graph.getTestItem("foo", "baz");

        HexMap hexMap = new HexMap();
        hexMap.add(item1, new MapTile(e1));
        hexMap.add(item2, new MapTile(e2));


        Set<MapTile> area = GroupAreaFactory.getGroup(hexMap, foo, Set.of(item1, item2));

        SVGGroupArea group = SVGGroupArea.forGroup(foo, area, false);
        Set<MapTile> groupArea = group.getGroupArea();

        //when
        HexGroupAreaOutlineFactory svgGroupAreaOutlineFactory = new HexGroupAreaOutlineFactory();
        List<Component> outline = svgGroupAreaOutlineFactory.getOutline(groupArea, "005500");

        //then
        assertNotNull(outline);
        assertEquals(248, outline.size());
    }

}