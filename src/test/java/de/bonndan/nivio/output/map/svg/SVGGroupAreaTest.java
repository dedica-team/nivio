package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.*;
import de.bonndan.nivio.output.map.hex.Hex;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SVGGroupAreaTest {

    @Test
    public void placesGroupNameAtLowest() {
        Set<Hex> occupied = new HashSet<>();
        occupied.add(new Hex(1, 1, -2));
        occupied.add(new Hex(3, 3, -6));

        Item landscapeItem = new Item();
        landscapeItem.setIdentifier("landscapeItem");

        Map<LandscapeItem, Hex> vertexHexes = Map.of(landscapeItem, new Hex(1, 2, -3));

        Item source = new Item();
        source.setIdentifier("source");
        Item target = new Item();
        target.setIdentifier("target");

        Group group = new Group("group");
        group.getItems().add(landscapeItem);

        SVGGroupArea svgGroupArea = SVGGroupAreaFactory.getGroup(occupied, group, vertexHexes, new ArrayList<SVGRelation>());

        assertTrue(svgGroupArea.render().render().contains("<text x=\"350.0\" y=\"916.217782649107\" font-size=\"24\" text-anchor=\"middle\" class=\"groupLabel\">group</text>"));
    }
}