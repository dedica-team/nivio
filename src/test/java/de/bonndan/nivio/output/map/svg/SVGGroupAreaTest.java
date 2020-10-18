package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.hex.GroupAreaFactory;
import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class SVGGroupAreaTest {

    @Test
    public void placesGroupNameAtLowest() {
        Set<Hex> occupied = new HashSet<>();
        occupied.add(new Hex(1, 1, -2));
        occupied.add(new Hex(3, 3, -6));

        Item landscapeItem = new Item("group", "landscapeItem");
        Map<Item, Hex> vertexHexes = Map.of(landscapeItem, new Hex(1, 2, -3));

        Group group = new Group("group");
        group.addItem(landscapeItem);

        Set<Hex> area = GroupAreaFactory.getGroup(occupied, group, vertexHexes);
        SVGGroupArea svgGroupArea = SVGGroupAreaFactory.getGroup(group, area, false);

        assertThat(svgGroupArea.render().render()).contains("<text x=\"350.0\" y=\"831\" text-anchor=\"middle\" class=\"groupLabel\">group</text>");
    }
}