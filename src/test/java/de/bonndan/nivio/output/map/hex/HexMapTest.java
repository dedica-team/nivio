package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.layout.LayoutedComponent;
import de.bonndan.nivio.output.map.svg.HexPath;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.assertj.core.api.Assertions.assertThat;

class HexMapTest {


    @Test
     void getPath() {
        Item bar = getTestItem("foo", "bar");
        LayoutedComponent barComponent = new LayoutedComponent(bar);
        barComponent.x = 0;
        barComponent.y = 0;

        Item baz = getTestItem("moo", "baz");
        LayoutedComponent bazComponent = new LayoutedComponent(baz);
        barComponent.x = 500;
        barComponent.y = 500;

        HexMap hexMap = new HexMap();
        hexMap.add(bar,hexMap.findFreeSpot(barComponent.getX(), barComponent.getY()));
        hexMap.add(baz,hexMap.findFreeSpot(bazComponent.getX(), bazComponent.getY()));

        //when
        Optional<HexPath> path = hexMap.getPath(bar, baz, true);

        //then
        assertThat(path).isNotEmpty();

        path.get().getMapTiles().forEach(hex -> assertThat(hex.getPathDirection()).isNotNull());
    }

    @Test
    void addCreatesHexWithItem() {
        Item bar = getTestItem("foo", "bar");
        LayoutedComponent barComponent = new LayoutedComponent(bar);
        barComponent.x = 0;
        barComponent.y = 0;

        HexMap hexMap = new HexMap();

        //when
        MapTile added = hexMap.add(bar,hexMap.findFreeSpot(barComponent.getX(), barComponent.getY()));

        //then
        assertThat(added).isNotNull();
        assertThat(added.getItem()).isEqualTo(bar.getFullyQualifiedIdentifier());
    }

    @Test
    void allAreaHexesHaveCorrectGroup() {
        MapTile one = new MapTile(new Hex(1, 1));
        MapTile two = new MapTile(new Hex(3, 3));

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item target = getTestItem("group", "target");

        Group group = new Group("group", "landscapeIdentifier");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(target);

        HexMap hexMap = new HexMap();
        hexMap.add(landscapeItem, one);
        hexMap.add(target, two);

        //when
        Set<MapTile> groupArea = hexMap.getGroupArea(group);

        //then
        long count = groupArea.stream().filter(hex -> hex.getGroup() != null).count();
        assertThat(count).isEqualTo(groupArea.size());
    }
}