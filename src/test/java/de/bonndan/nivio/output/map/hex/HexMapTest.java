package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.GraphTestSupport;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.output.map.hex.Hex.NORTH;
import static de.bonndan.nivio.output.map.hex.Hex.NORTH_WEST;
import static org.assertj.core.api.Assertions.assertThat;

class HexMapTest {


    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
    }

    @Test
    void getPath() {
        Item bar = getTestItem("foo", "bar");

        LayoutedComponent barComponent = new LayoutedComponent(bar, Collections.emptyList());
        barComponent.setCenterX(0);
        barComponent.setCenterY(0);

        Item baz = getTestItem("moo", "baz");
        LayoutedComponent bazComponent = new LayoutedComponent(baz, Collections.emptyList());
        barComponent.setCenterX(500);
        barComponent.setCenterY(500);

        HexMap hexMap = new HexMap();
        hexMap.add(bar, hexMap.findFreeSpot(barComponent));
        hexMap.add(baz, hexMap.findFreeSpot(bazComponent));

        //when
        Optional<HexPath> path = hexMap.getPath(bar, baz, true);

        //then
        assertThat(path).isNotEmpty();

        List<PathTile> tiles = path.get().getTiles();
        tiles.forEach(pathTile -> assertThat(pathTile.getMapTile().getPathDirections()).isNotNull());

        List<Integer> pathTileDirs = path.get().getDirections();
        assertThat(pathTileDirs).isEqualTo(List.of(NORTH_WEST, NORTH_WEST, NORTH_WEST, NORTH, NORTH, NORTH));


        PathTile port = tiles.get(tiles.size() - 2);
        assertThat(port.getMapTile().incrementPortCount()).isEqualTo(1);

    }

    @Test
    void addCreatesHexWithItem() {
        Item bar = getTestItem("foo", "bar");
        LayoutedComponent barComponent = new LayoutedComponent(bar, Collections.emptyList());
        barComponent.setCenterX(0);
        barComponent.setCenterY(0);

        HexMap hexMap = new HexMap();

        //when
        MapTile added = hexMap.add(bar, hexMap.findFreeSpot(barComponent));

        //then
        assertThat(added).isNotNull();
        assertThat(added.getItem()).isEqualTo(bar.getFullyQualifiedIdentifier());
    }

    @Test
    void allAreaHexesHaveCorrectGroup() {
        MapTile one = new MapTile(new Hex(1, 1));
        MapTile two = new MapTile(new Hex(3, 3));


        HexMap hexMap = new HexMap();
        hexMap.add(graph.itemAA, one);
        hexMap.add(graph.itemAB, two);

        //when
        Set<MapTile> groupArea = hexMap.getGroupArea(graph.groupA, Set.of(graph.itemAA, graph.itemAB));

        //then
        long count = groupArea.stream().filter(hex -> hex.getGroup() != null).count();
        assertThat(count).isEqualTo(groupArea.size());
    }
}