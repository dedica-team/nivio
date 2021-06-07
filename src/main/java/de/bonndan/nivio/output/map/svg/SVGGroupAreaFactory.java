package de.bonndan.nivio.output.map.svg;

import com.google.common.collect.Lists;
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

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

/**
 * Collects all hexes close to group item hexes to create an area.
 */
public class SVGGroupAreaFactory {

    /**
     * Builds an areas of hex tiles belonging to a group.
     *
     * @param group         the group
     * @param inArea        all hex tiles forming an area
     * @param groupStatuses assessment statuses of the group and its children
     * @param debug         turn on debugging
     */
    public static SVGGroupArea getGroup(@NonNull final Group group,
                                        @NonNull final Set<Hex> inArea,
                                        @NonNull final List<StatusValue> groupStatuses,
                                        boolean debug
    ) {

        var fill = Objects.requireNonNull(group).getColor();
        var fillId = fill != null ? "#" + fill : "";

        SVGGroupAreaOutlineFactory outlineFactory = new SVGGroupAreaOutlineFactory();
        outlineFactory.setDebug(debug);
        List<DomContent> statusShadow = outlineFactory.getOutline(inArea, fillId);
        if (statusShadow.size() > 0) {
            StatusValue worst = Assessable.getWorst(groupStatuses);
            ContainerTag svgPath = (ContainerTag) statusShadow.get(0);
            svgPath.attr("stroke", worst.getStatus().getName())
                    .attr("stroke-width", 5 + SVGStatus.getAddedStroke(worst))
                    .attr("fill", worst.getStatus().getName())
                    .attr("filter", "url(#" + SVGStatus.GLOW_FILTER_ID + ")");
        }

        List<DomContent> outlines = outlineFactory.getOutline(inArea, fillId);
        statusShadow.addAll(outlines);
        return new SVGGroupArea(group, inArea, statusShadow);
    }
}
