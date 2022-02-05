package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.GraphTestSupport;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupAreaFactoryTest {

    private final Set<MapTile> expectedTerritory = UnmodifiableSet.unmodifiableSet(Set.of(
            new MapTile(new Hex(0, 3)),
            new MapTile(new Hex(1, 3)),
            new MapTile(new Hex(1, 2)),
            new MapTile(new Hex(2, 2)),
            new MapTile(new Hex(0, 2)),
            new MapTile(new Hex(2, 1)),
            new MapTile(new Hex(1, 1))
    ));
    private GraphTestSupport graph;

    @BeforeEach
    void setup() {
        graph = new GraphTestSupport();
    }

    /**
     * https://www.redblobgames.com/grids/hexagons/#coordinates
     */
    @Test
    void getBridges() {
        Set<MapTile> inArea = new HashSet<>();
        //vertical with one hex gap
        inArea.add(new MapTile(new Hex(3, 1)));
        inArea.add(new MapTile(new Hex(3, 3)));

        HexMap hexMap = new HexMap();

        //when
        Set<MapTile> bridges = GroupAreaFactory.getBridges(hexMap, inArea, 2);
        assertEquals(1, bridges.size());
        assertEquals(new Hex(3, 2), bridges.iterator().next().getHex());
    }

    @Test
    void addHexesWithClosestPaths() {
        MapTile one = new MapTile(new Hex(1, 2));
        MapTile two = new MapTile(new Hex(3, 5));


        HexMap hexMap = new HexMap();
        hexMap.add(graph.itemAA, one);
        hexMap.add(graph.itemAB, two);

        //when
        Set<MapTile> inArea = GroupAreaFactory.getGroup(hexMap, graph.groupA, Set.of(graph.itemAA, graph.itemAB));

        //then
        assertThat(inArea).containsAll(expectedTerritory);

        PathFinder pathFinder = new PathFinder(hexMap, true);
        HexPath shortestPath = pathFinder.getPath(one, two).orElseThrow();
        assertThat(inArea).containsAll(shortestPath.getTiles().stream().map(PathTile::getMapTile).collect(Collectors.toList()));

    }

    @Test
    void justAddsHexAndNeighbours() {
        MapTile one = new MapTile(new Hex(1, 2));
        MapTile two = new MapTile(new Hex(3, 3));

        HexMap hexMap = new HexMap();
        hexMap.add(graph.itemAA, one);
        hexMap.add(graph.itemAA, two);

        //when
        Set<MapTile> inArea = GroupAreaFactory.getGroup(hexMap, graph.groupA, Set.of(graph.itemAA));

        //then
        assertThat(inArea).isEqualTo(expectedTerritory);
    }

    @Test
    void doesNotAddUnnecessaryTiles() {

        MapTile one = new MapTile(new Hex(4, 4));
        MapTile two = new MapTile(new Hex(6, 4));

        HexMap hexMap = new HexMap();
        hexMap.add(graph.itemAA, one);
        hexMap.add(graph.itemAB, two);

        //when
        Set<MapTile> inArea = GroupAreaFactory.getGroup(hexMap, graph.groupA, Set.of(graph.itemAA, graph.itemAB));
        assertThat(inArea).doesNotContain(new MapTile(new Hex(5, 2)));
        assertThat(inArea).doesNotContain(new MapTile(new Hex(6, 2)));
        assertThat(inArea).doesNotContain(new MapTile(new Hex(7, 2)));
    }

    @Test
    void pathToClosestItemIsPaddedByOneHex() {

        MapTile one = new MapTile(new Hex(4, 4));
        MapTile two = new MapTile(new Hex(7, 4));

        HexMap hexMap = new HexMap();
        hexMap.add(graph.itemAA, one);
        hexMap.add(graph.itemAB, two);

        //when
        Set<MapTile> inArea = GroupAreaFactory.getGroup(hexMap, graph.groupA, Set.of(graph.itemAA, graph.itemAB));

        //then
        assertThat(inArea).contains(new MapTile(new Hex(6, 3)));
        assertThat(inArea).contains(new MapTile(new Hex(5, 5)));
    }

}
