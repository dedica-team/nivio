package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.hex.GroupAreaFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexMap;
import de.bonndan.nivio.output.map.hex.MapTile;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static de.bonndan.nivio.output.map.svg.SVGDocument.DATA_IDENTIFIER;
import static de.bonndan.nivio.output.map.svg.SVGDocument.VISUAL_FOCUS_UNSELECTED;
import static org.assertj.core.api.Assertions.assertThat;

class SVGGroupAreaTest {

    @Test
    void hasFQI() {
        MapTile e1 = new MapTile(new Hex(1, 1));
        MapTile e2 = new MapTile(new Hex(3, 3));

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item landscapeItem2 = getTestItem("group", "bar");

        Group group = new Group("group", "foo");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(landscapeItem2);

        HexMap hexMap = new HexMap();
        hexMap.add(landscapeItem, e1);
        hexMap.add(landscapeItem2, e2);


        Set<MapTile> area = GroupAreaFactory.getGroup(hexMap, group, Set.of(landscapeItem, landscapeItem2));
        SVGGroupArea svgGroupArea = SVGGroupArea.forGroup(group, area, false);

        assertThat(svgGroupArea.render().render()).contains(group.getFullyQualifiedIdentifier().jsonValue());
    }

    @Test
    void supportsVisualFocus() {
        MapTile e1 = new MapTile(new Hex(1, 1));
        MapTile e2 = new MapTile(new Hex(3, 3));

        Item landscapeItem = getTestItem("group", "landscapeItem");
        Item landscapeItem2 = getTestItem("group", "bar");

        Group group = new Group("group", "foo");
        group.addOrReplaceItem(landscapeItem);
        group.addOrReplaceItem(landscapeItem2);

        HexMap hexMap = new HexMap();
        hexMap.add(landscapeItem, e1);
        hexMap.add(landscapeItem2, e2);

        Set<MapTile> area = GroupAreaFactory.getGroup(hexMap, group, Set.of(landscapeItem, landscapeItem2));
        SVGGroupArea svgGroupArea = SVGGroupArea.forGroup(group, area, false);

        //then
        String render1 = svgGroupArea.render().render();
        assertThat(render1).contains(DATA_IDENTIFIER).contains(VISUAL_FOCUS_UNSELECTED);
    }


}