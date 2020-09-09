package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.hex.Hex;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SVGGroupAreaFactoryTest {

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
        Set<Hex> bridges = SVGGroupAreaFactory.getBridges(inArea);
        assertEquals(1, bridges.size());
        assertEquals(new Hex(3,2,-5), bridges.iterator().next());
    }

    @Test
    public void getGroup_addsItemToInnerRelationsIfItIsTheSourceItem() {
        Set<Hex> occupied = new HashSet<>();
        occupied.add(new Hex(1, 1, -2));
        occupied.add(new Hex(3, 3, -6));

        Item landscapeItem = new Item("group", "landscapeItem");

        Map<LandscapeItem, Hex> vertexHexes = Map.of(landscapeItem, new Hex(1, 2, -3));
        Hex landscapeItemHex = new Hex(4, 5, -9);
        HexPath hexPath = new HexPath(List.of(landscapeItemHex));

        Item target = new Item("group", "target");
        RelationItem<Item> relation = new Relation(landscapeItem, target);
        SVGRelation svgRelation = new SVGRelation(hexPath, "blue", relation);
        List<SVGRelation> relations = List.of(svgRelation);

        Group group = new Group("group");
        group.addItem(landscapeItem);

        SVGGroupArea svgGroupArea = SVGGroupAreaFactory.getGroup(occupied, group, vertexHexes, relations);

        assertThat(svgGroupArea.group).isEqualTo(group);
        assertThat(svgGroupArea.groupArea).containsAll(expectedTerritory);
        assertThat(svgGroupArea.groupArea).contains(landscapeItemHex);
    }

    @Test
    public void getGroup_doesntAddItemToInnerRelationsIfTheTargetHasADifferentGroup() {
        Set<Hex> occupied = new HashSet<>();
        occupied.add(new Hex(1, 1, -2));
        occupied.add(new Hex(3, 3, -6));

        Item landscapeItem = new Item("group", "landscapeItem");

        Map<LandscapeItem, Hex> vertexHexes = Map.of(landscapeItem, new Hex(1, 2, -3));
        Hex landscapeItemHex = new Hex(4, 5, -9);
        HexPath hexPath = new HexPath(List.of(landscapeItemHex));

        Item target = new Item("otherGroup", "target");
        RelationItem<Item> relation = new Relation(landscapeItem, target);
        SVGRelation svgRelation = new SVGRelation(hexPath, "blue", relation);
        List<SVGRelation> relations = List.of(svgRelation);

        Group group = new Group("group");
        group.addItem(landscapeItem);

        SVGGroupArea svgGroupArea = SVGGroupAreaFactory.getGroup(occupied, group, vertexHexes, relations);

        assertThat(svgGroupArea.group).isEqualTo(group);
        assertThat(svgGroupArea.groupArea).isEqualTo(expectedTerritory);
    }

    @Test
    public void getGroup_doesntAddItemToInnerRelationsIfTheTargetHasNoGroup() {
        Set<Hex> occupied = new HashSet<>();
        occupied.add(new Hex(1, 1, -2));
        occupied.add(new Hex(3, 3, -6));

        Item landscapeItem = new Item("group", "landscapeItem");

        Map<LandscapeItem, Hex> vertexHexes = Map.of(landscapeItem, new Hex(1, 2, -3));
        Hex landscapeItemHex = new Hex(4, 5, -9);
        HexPath hexPath = new HexPath(List.of(landscapeItemHex));

        Item target = new Item(null, "target");
        RelationItem<Item> relation = new Relation(landscapeItem, target);
        SVGRelation svgRelation = new SVGRelation(hexPath, "blue", relation);
        List<SVGRelation> relations = List.of(svgRelation);

        Group group = new Group("group");
        group.addItem(landscapeItem);

        SVGGroupArea svgGroupArea = SVGGroupAreaFactory.getGroup(occupied, group, vertexHexes, relations);

        assertThat(svgGroupArea.group).isEqualTo(group);
        assertThat(svgGroupArea.groupArea).isEqualTo(expectedTerritory);
    }

    @Test
    public void getGroup_doesntAddItemToInnerRelationsIfItIsNotTheSourceItem() {
        Set<Hex> occupied = new HashSet<>();
        occupied.add(new Hex(1, 1, -2));
        occupied.add(new Hex(3, 3, -6));

        Item landscapeItem = new Item("group", "landscapeItem");

        Map<LandscapeItem, Hex> vertexHexes = Map.of(landscapeItem, new Hex(1, 2, -3));
        HexPath hexPath = new HexPath(List.of(new Hex(4, 5, -9)));

        Item source = new Item(null,"source");
        Item target = new Item(null, "target");
        RelationItem<Item> relation = new Relation(source, target);
        SVGRelation svgRelation = new SVGRelation(hexPath, "blue", relation);
        List<SVGRelation> relations = List.of(svgRelation);

        Group group = new Group("group");
        group.addItem(landscapeItem);

        SVGGroupArea svgGroupArea = SVGGroupAreaFactory.getGroup(occupied, group, vertexHexes, relations);

        assertThat(svgGroupArea.group).isEqualTo(group);
        assertThat(svgGroupArea.groupArea).isEqualTo(expectedTerritory);
    }
}
