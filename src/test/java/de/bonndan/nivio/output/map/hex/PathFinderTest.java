package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PathFinderTest {

    private HexMap hexMap;
    private MapTile one;
    private MapTile two;

    @BeforeEach
    void setup() {
        one = new MapTile(new Hex(1, 2));
        two = new MapTile(new Hex(3, 5));

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");
        Item target2 = getTestItem("group", "target2");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(target);
        group.addOrReplaceItem(target2);

        hexMap = new HexMap();
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
        assertThat(path.get().getTiles()).hasSize(6)
                .contains(new PathTile(one))
                .contains(new PathTile(new MapTile(new Hex(2, 2))))
                .contains(new PathTile(two))
        ;
    }

    @Test
    void evadesObstacle() {

        //given
        MapTile obstacle = new MapTile(new Hex(2, 5));

        Item target2 = getTestItem("group", "target2");
        obstacle.setItem(target2.getFullyQualifiedIdentifier());

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(target2);

        hexMap.add(target2, obstacle);

        PathFinder pathFinder = new PathFinder(hexMap, true);

        //when
        Optional<HexPath> path = pathFinder.getPath(one, two);

        //then
        assertThat(path).isPresent();
        assertThat(path.get().getTiles()).hasSize(6).doesNotContain(new PathTile(obstacle));
    }

    @Test
    void noExtraCost() {
        MapTile fromHex = new MapTile(new Hex(3,2));
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        MapTile toHex = new MapTile(new Hex(3,3));
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(2, costs);
    }

    @Test
    void pathCosts() {
        MapTile fromHex = new MapTile(new Hex(3,2));
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        MapTile toHex =  new MapTile(new Hex(3,3));
        toHex.addPathDirection(1);
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(2, costs); //no path costs
    }

    @Test
    void groupCostMore() {
        MapTile fromHex = new MapTile(new Hex(3,2));
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        MapTile toHex = new MapTile(new Hex(3,3));
        toHex.setGroup("bar");
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(4, costs);
    }

    @Test
    void groupCostSameIfComingFromGroup() {
        MapTile fromHex = new MapTile(new Hex(3,2));
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;
        from.mapTile.setGroup("foo/other");

        MapTile toHex = new MapTile(new Hex(3,3));
        toHex.setGroup("foo/bar");
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(3, costs);
    }

    @Test
    void itemsBlock() {
        MapTile fromHex = new MapTile(new Hex(3,2));
        PathTile from = new PathTile(fromHex);
        from.moveCosts = 1;

        Item block = getTestItem("foo", "block");
        MapTile toHex = new MapTile(new Hex(3,3));
        toHex.setGroup(block.getGroup());
        toHex.setItem(block.getFullyQualifiedIdentifier());
        PathTile to = new PathTile(toHex);

        //when
        int costs = PathFinder.calcMoveCostsFrom(from, to);
        assertEquals(1001, costs);
    }
}