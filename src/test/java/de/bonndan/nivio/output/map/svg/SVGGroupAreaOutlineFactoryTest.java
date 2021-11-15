package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.hex.GroupAreaFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexMap;
import de.bonndan.nivio.output.map.hex.MapTile;
import j2html.tags.DomContent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SVGGroupAreaOutlineFactoryTest {

    @Test
    @DisplayName("Ensure that items far apart have one outline")
    void twoSeparateHexes() {
        Hex e1 = new Hex(0, 100);
        Hex e2 = new Hex(0, 20);

        Item item1 = getTestItem("foo", "bar");
        Item item2 = getTestItem("foo", "baz");

        Group foo = new Group("foo", "landscapeIdentifier", null, null, null, "005500");
        foo.addOrReplaceItem(item1);
        foo.addOrReplaceItem(item2);

        HexMap hexMap = new HexMap();
        hexMap.add(item1, new MapTile(e1));
        hexMap.add(item2, new MapTile(e2));


        Set<MapTile> area = GroupAreaFactory.getGroup(hexMap, foo, Set.of(item1, item2));

        SVGGroupArea group = SVGGroupArea.forGroup(foo, area,  Status.GREEN, false);
        Set<MapTile> groupArea = group.getGroupArea();

        //when
        SVGGroupAreaOutlineFactory svgGroupAreaOutlineFactory = new SVGGroupAreaOutlineFactory(SVGGroupAreaOutlineFactory.GroupAreaStyle.WOBBLY);
        List<DomContent> outline = svgGroupAreaOutlineFactory.getOutline(groupArea, "005500");

        //then
        assertNotNull(outline);
        assertEquals(1, outline.size());
    }

}