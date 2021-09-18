package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupAreaFactoryTest {

    private final Set<Hex> expectedTerritory = UnmodifiableSet.unmodifiableSet(Set.of(
            new Hex(0, 3),
            new Hex(1, 3),
            new Hex(1, 2),
            new Hex(2, 2),
            new Hex(0, 2),
            new Hex(2, 1),
            new Hex(1, 1)
    ));

    /**
     * https://www.redblobgames.com/grids/hexagons/#coordinates
     */
    @Test
    void getBridges() {
        Set<Hex> inArea = new HashSet<>();
        //vertical with one hex gap
        inArea.add(new Hex(3, 1));
        inArea.add(new Hex(3, 3));

        //when
        Set<Hex> bridges = GroupAreaFactory.getBridges(inArea, 2);
        assertEquals(1, bridges.size());
        assertEquals(new Hex(3, 2), bridges.iterator().next());
    }

    @Test
    void addHexesWithClosestPaths() {
        Hex one = new Hex(1, 2);
        Hex two = new Hex(3, 5);

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(target);

        BidiMap<Hex, Object> hexesToItems = new DualHashBidiMap<>();
        hexesToItems.put(one, landscapeItem);
        hexesToItems.put(two, target);

        HexMap hexMap = new HexMap(true);
        hexMap.add(landscapeItem, one);
        hexMap.add(target, two);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(hexesToItems.inverseBidiMap(), group);

        //then
        assertThat(inArea).containsAll(expectedTerritory);

        PathFinder pathFinder = new PathFinder(hexMap);
        HexPath shortestPath = pathFinder.getPath(one, two).orElseThrow();
        assertThat(inArea).containsAll(shortestPath.getHexes());

    }

    @Test
    void justAddsHexAndNeighbours() {
        Hex one = new Hex(1, 2);
        Hex two = new Hex(3, 3);

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);

        BidiMap<Hex, Object> hexesToItems = new DualHashBidiMap<>();
        hexesToItems.put(one, landscapeItem);
        hexesToItems.put(two, target);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(hexesToItems.inverseBidiMap(), group);

        //then
        assertThat(inArea).isEqualTo(expectedTerritory);
    }

    @Test
    void doesNotAddUnnecessaryTiles() {

        Hex one = new Hex(4, 4);
        Hex two = new Hex(6, 4);

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);

        BidiMap<Hex, Object> hexesToItems = new DualHashBidiMap<>();
        hexesToItems.put(one, landscapeItem);
        hexesToItems.put(two, target);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(hexesToItems.inverseBidiMap(), group);
        assertThat(inArea).doesNotContain(new Hex(5, 2));
        assertThat(inArea).doesNotContain(new Hex(6, 2));
        assertThat(inArea).doesNotContain(new Hex(7, 2));
    }

    @Test
    void pathToClosestItemIsPaddedByOneHex() {

        Hex one = new Hex(4, 4);
        Hex two = new Hex(7, 4);

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(target);

        BidiMap<Hex, Object> hexesToItems = new DualHashBidiMap<>();
        hexesToItems.put(one, landscapeItem);
        hexesToItems.put(two, target);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(hexesToItems.inverseBidiMap(), group);

        //then
        assertThat(inArea).contains(new Hex(6, 3));
        assertThat(inArea).contains(new Hex(5, 5));
    }

}
