package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PathFinderTest {

    private HexMap hexMap;
    private Hex one;
    private Hex two;

    @BeforeEach
    void setup() {
        one = new Hex(1, 2);
        two = new Hex(3, 5);

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");
        Item target2 = getTestItem("group", "target2");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(target);
        group.addOrReplaceItem(target2);

        hexMap = new HexMap(true);
        hexMap.add(landscapeItem, one);
        hexMap.add(target, two);
        hexMap.add(target, two);
    }

    @Test
    void getPath() {

        //given
        PathFinder pathFinder = new PathFinder(hexMap, true);

        //when
        Optional<HexPath> path = pathFinder.getPath(one, two);

        //then
        assertThat(path).isPresent();
        assertThat(path.get().getHexes()).hasSize(6)
                .contains(new Hex(2, 2))
                .contains(new Hex(3, 4))
        ;
    }

    @Test
    void evadesObstacle() {

        //given
        Hex obstacle = new Hex(2, 5);

        Item target2 = getTestItem("group", "target2");
        obstacle.item = target2.getFullyQualifiedIdentifier().toString();

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(target2);

        hexMap.add(target2, obstacle);

        PathFinder pathFinder = new PathFinder(hexMap, true);

        //when
        Optional<HexPath> path = pathFinder.getPath(one, two);

        //then
        assertThat(path).isPresent();
        assertThat(path.get().getHexes()).hasSize(6).doesNotContain(obstacle);
    }

    @Test
    void noExtraCost() {
        Hex fromHex = new Hex(3,2);
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        Hex toHex = new Hex(3,3);
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(2, costs);
    }

    @Test
    void pathCosts() {
        Hex fromHex = new Hex(3,2);
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        Hex toHex = new Hex(3,3);
        toHex.setPathDirection(1);
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(3, costs);
    }

    @Test
    void groupCostMore() {
        Hex fromHex = new Hex(3,2);
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        Hex toHex = new Hex(3,3);
        toHex.group = "foo/bar";
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(4, costs);
    }

    @Test
    void groupCostSameIfComingFromGroup() {
        Hex fromHex = new Hex(3,2);
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;
        from.hex.group = "foo/other";

        Hex toHex = new Hex(3,3);
        toHex.group = "foo/bar";
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(2, costs);
    }

    @Test
    void itemsBlock() {
        Hex fromHex = new Hex(3,2);
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        Hex toHex = new Hex(3,3);
        toHex.item = "foo/bar/baz";
        toHex.group = "foo/bar";
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(1001, costs);
    }
}