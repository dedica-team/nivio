package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.hex.GroupAreaFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.HexMap;
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

        Group foo = new Group("foo", "landscapeIdentifier", null, null, null, null, "005500");
        foo.addOrReplaceItem(item1);
        foo.addOrReplaceItem(item2);

        HexMap hexMap = new HexMap(true);
        hexMap.add(item1, e1);
        hexMap.add(item2, e2);


        Set<Hex> area = GroupAreaFactory.getGroup(hexMap, foo);

        SVGGroupArea group = SVGGroupArea.forGroup(foo, area, new StatusValue("foo", Status.GREEN), false);
        Set<Hex> groupArea = group.getGroupArea();

        //when
        SVGGroupAreaOutlineFactory svgGroupAreaOutlineFactory = new SVGGroupAreaOutlineFactory(SVGGroupAreaOutlineFactory.GroupAreaStyle.WOBBLY);
        List<DomContent> outline = svgGroupAreaOutlineFactory.getOutline(groupArea, "005500");

        //then
        assertNotNull(outline);
        assertEquals(1, outline.size());
    }

}