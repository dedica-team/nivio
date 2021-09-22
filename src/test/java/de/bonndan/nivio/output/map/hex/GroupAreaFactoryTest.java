package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
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

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(target);

        HexMap hexMap = new HexMap();
        hexMap.add(landscapeItem, one);
        hexMap.add(target, two);

        //when
        Set<MapTile> inArea = GroupAreaFactory.getGroup(hexMap, group);

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

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);

        HexMap hexMap = new HexMap();
        hexMap.add(landscapeItem, one);
        hexMap.add(target, two);

        //when
        Set<MapTile> inArea = GroupAreaFactory.getGroup(hexMap, group);

        //then
        assertThat(inArea).isEqualTo(expectedTerritory);
    }

    @Test
    void doesNotAddUnnecessaryTiles() {

        MapTile one = new MapTile(new Hex(4, 4));
        MapTile two = new MapTile(new Hex(6, 4));

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);

        HexMap hexMap = new HexMap();
        hexMap.add(landscapeItem, one);
        hexMap.add(target, two);

        //when
        Set<MapTile> inArea = GroupAreaFactory.getGroup(hexMap, group);
        assertThat(inArea).doesNotContain(new MapTile(new Hex(5, 2)));
        assertThat(inArea).doesNotContain(new MapTile(new Hex(6, 2)));
        assertThat(inArea).doesNotContain(new MapTile(new Hex(7, 2)));
    }

    @Test
    void pathToClosestItemIsPaddedByOneHex() {

        MapTile one = new MapTile(new Hex(4, 4));
        MapTile two = new MapTile(new Hex(7, 4));

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(target);

        HexMap hexMap = new HexMap();
        hexMap.add(landscapeItem, one);
        hexMap.add(target, two);

        //when
        Set<MapTile> inArea = GroupAreaFactory.getGroup(hexMap, group);

        //then
        assertThat(inArea).contains(new MapTile(new Hex(6, 3)));
        assertThat(inArea).contains(new MapTile(new Hex(5, 5)));
    }

}
