package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupAreaFactoryTest {

    private final Set<Hex> expectedTerritory = UnmodifiableSet.unmodifiableSet(Set.of(
            new Hex(0, 3, -3),
            new Hex(1, 3, -4),
            new Hex(1, 2, -3),
            new Hex(2, 2, -4),
            new Hex(0, 2, -2),
            new Hex(2, 1, -3),
            new Hex(1, 1, -2)
    ));

    /**
     * https://www.redblobgames.com/grids/hexagons/#coordinates
     */
    @Test
    public void getBridges() {
        Set<Hex> inArea = new HashSet<>();
        //vertical with one hex gap
        inArea.add(new Hex(3, 1, -4));
        inArea.add(new Hex(3, 3, -6));

        //when
        Set<Hex> bridges = GroupAreaFactory.getBridges(inArea, 2);
        assertEquals(1, bridges.size());
        assertEquals(new Hex(3, 2, -5), bridges.iterator().next());
    }

    @Test
    public void addHexesWithClosestPaths() {
        Set<Hex> occupied = new HashSet<>();
        occupied.add(new Hex(1, 1, -2));
        occupied.add(new Hex(3, 3, -6));

        Item landscapeItem = new Item("group", "landscapeItem");
        Item target = new Item("group", "target");

        Map<Item, Hex> vertexHexes = Map.of(
                landscapeItem, new Hex(1, 2, -3),
                target, new Hex(3, 5, -8)
        );

        Group group = new Group("group");
        group.addItem(landscapeItem);
        group.addItem(target);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(occupied, group, vertexHexes);

        //then
        assertThat(inArea).containsAll(expectedTerritory);

        PathFinder pathFinder = new PathFinder(occupied);
        HexPath shortestPath = pathFinder.getPath(vertexHexes.get(landscapeItem), vertexHexes.get(target)).orElseThrow();
        assertThat(inArea).containsAll(shortestPath.getHexes());

    }

    @Test
    public void justAddsHexAndNeighbours() {
        Set<Hex> occupied = new HashSet<>();
        occupied.add(new Hex(1, 1, -2));
        occupied.add(new Hex(3, 3, -6));

        Item landscapeItem = new Item("group", "landscapeItem");

        Map<Item, Hex> vertexHexes = Map.of(landscapeItem, new Hex(1, 2, -3));

        Group group = new Group("group");
        group.addItem(landscapeItem);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(occupied, group, vertexHexes);

        //then
        assertThat(inArea).isEqualTo(expectedTerritory);
    }

    @Test
    public void doesNotAddUnnecessaryTiles() {
        Item one = new Item("group", "one");
        Item two = new Item("group", "two");

        Map<Item, Hex> vertexHexes = Map.of(
                one, new Hex(4, 4),
                two, new Hex(6, 4)
        );

        Group group = new Group("group");
        group.addItem(one);
        group.addItem(two);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(Set.of(), group, vertexHexes);
        assertThat(inArea).doesNotContain(new Hex(5,2));
        assertThat(inArea).doesNotContain(new Hex(6,2));
        assertThat(inArea).doesNotContain(new Hex(7,2));
    }

    @Test
    public void pathToClosestItemIsPaddedByOneHex() {
        Item one = new Item("group", "one");
        Item two = new Item("group", "two");

        Map<Item, Hex> vertexHexes = Map.of(
                one, new Hex(4, 4),
                two, new Hex(7, 4)
        );

        Group group = new Group("group");
        group.addItem(one);
        group.addItem(two);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(Set.of(), group, vertexHexes);
        assertThat(inArea).contains(new Hex(6,3));
        assertThat(inArea).contains(new Hex(5,5));
    }

    @Test
    public void allAreaHexesHaveCorrectGroup() {
        Item one = new Item("group", "one");
        Item two = new Item("group", "two");

        Hex hexOne = new Hex(4, 4);
        hexOne.item = one.getFullyQualifiedIdentifier().toString();
        Hex hexTwo = new Hex(7, 4);
        hexTwo.item = two.getFullyQualifiedIdentifier().toString();

        Map<Item, Hex> vertexHexes = Map.of(
                one, hexOne,
                two, hexTwo
        );

        Group group = new Group("group");
        group.addItem(one);
        group.addItem(two);

        //when
        Set<Hex> inArea = GroupAreaFactory.getGroup(Set.of(hexOne, hexTwo), group, vertexHexes);
        long count = inArea.stream().filter(hex -> hex.group != null).count();
        assertThat(count).isEqualTo(inArea.size());
    }
}
