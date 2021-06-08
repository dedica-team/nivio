package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Assessable;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Collects all hexes close to group item hexes to create an area.
 */
public class SVGGroupAreaFactory {

    /**
     * Builds an areas of hex tiles belonging to a group.
     *
     * @param group       the group
     * @param inArea      all hex tiles forming an area
     * @param groupStatus assessment status summary of the group
     * @param debug       turn on debugging
     */
    public static SVGGroupArea getGroup(@NonNull final Group group,
                                        @NonNull final Set<Hex> inArea,
                                        @NonNull final StatusValue groupStatus,
                                        boolean debug
    ) {
        var fill = Objects.requireNonNull(group).getColor();
        var fillId = fill != null ? "#" + fill : "";

        SVGGroupAreaOutlineFactory outlineFactory = new SVGGroupAreaOutlineFactory();
        outlineFactory.setDebug(debug);

        List<DomContent> outlines = outlineFactory.getOutline(inArea, fillId);
        return new SVGGroupArea(group, inArea, outlines, groupStatus);
    }
}
