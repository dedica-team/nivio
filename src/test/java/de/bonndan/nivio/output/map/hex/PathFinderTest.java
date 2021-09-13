package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;

class PathFinderTest {

    @Test
    void getPath() {

        //given
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

        PathFinder pathFinder = new PathFinder(hexesToItems);

        //when
        Optional<HexPath> path = pathFinder.getPath(one, two);

        //then
        assertThat(path).isPresent();
        assertThat(path.get().getHexes()).hasSize(6).contains(new Hex(2,5));
    }

    @Test
    void evadesObstacle() {

        //given
        Hex one = new Hex(1, 2);
        Hex two = new Hex(3, 5);
        Hex obstacle = new Hex(2, 5);

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");
        Item target2 = getTestItem("group", "target2");
        obstacle.item = target2.getFullyQualifiedIdentifier().toString();

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(target);
        group.addOrReplaceItem(target2);

        BidiMap<Hex, Object> hexesToItems = new DualHashBidiMap<>();
        hexesToItems.put(one, landscapeItem);
        hexesToItems.put(two, target);
        hexesToItems.put(obstacle, target2);

        PathFinder pathFinder = new PathFinder(hexesToItems);

        //when
        Optional<HexPath> path = pathFinder.getPath(one, two);

        //then
        assertThat(path).isPresent();
        assertThat(path.get().getHexes()).hasSize(6).doesNotContain(obstacle);
    }
}