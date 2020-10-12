package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Set;

/**
 * Collects all hexes close to group item hexes to create an area.
 */
public class SVGGroupAreaFactory {

    /**
     * Builds an areas of hex tiles belonging to a group.
     *
     * @param group  the group
     * @param inArea all hex tiles forming an area
     */
    public static SVGGroupArea getGroup(Group group, Set<Hex> inArea) {

        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        SVGGroupAreaOutlineFactory outlineFactory = new SVGGroupAreaOutlineFactory();
        //outlineFactory.setDebug(true);
        List<DomContent> outlines = outlineFactory.getOutline(inArea, fillId);

        return new SVGGroupArea(group, inArea, outlines);
    }
}
