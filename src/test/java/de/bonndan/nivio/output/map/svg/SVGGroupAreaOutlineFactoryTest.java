package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.model.Item;
import de.bonndan.nivio.output.map.hex.GroupAreaFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.bonndan.nivio.model.ItemFactory.getTestItem;
import static org.junit.jupiter.api.Assertions.*;

class SVGGroupAreaOutlineFactoryTest {

    @Test
    @DisplayName("Ensure that items far apart have one outline")
    public void twoSeparateHexe() {
        Hex e1 = new Hex(0, 10, -10);
        Hex e2 = new Hex(0, 20, -20);
        Set<Hex> occupied = Set.of(e1, e2);

        Item item1 = getTestItem("foo", "bar");
        Item item2 = getTestItem("foo", "baz");

        Group foo = new Group("foo", "landscapeIdentifier", null, null, null, null, "005500");
        foo.addItem(item1);
        foo.addItem(item2);

        BidiMap<Hex, Object> hexesToItems = new DualHashBidiMap<>();
        hexesToItems.put(e1, item1);
        hexesToItems.put(e2, item2);


        Set<Hex> area = GroupAreaFactory.getGroup(hexesToItems.inverseBidiMap(), foo);

        SVGGroupArea group = SVGGroupAreaFactory.getGroup(foo, area, false);
        Set<Hex> groupArea = group.getGroupArea();

        //when
        SVGGroupAreaOutlineFactory svgGroupAreaOutlineFactory = new SVGGroupAreaOutlineFactory();
        List<DomContent> outline = svgGroupAreaOutlineFactory.getOutline(groupArea, "005500");

        //then
        assertNotNull(outline);
        assertEquals(1, outline.size());
    }

}