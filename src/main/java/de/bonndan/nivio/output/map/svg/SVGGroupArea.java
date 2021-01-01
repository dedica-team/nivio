package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.DomContent;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Displays a group as an area containing items.
 */
class SVGGroupArea extends Component {

    private final Group group;
    final Set<Hex> groupArea;
    private final List<DomContent> outlines;

    SVGGroupArea(Group group, Set<Hex> groupArea, List<DomContent> outlines) {
        this.group = group;
        this.groupArea = groupArea;
        this.outlines = outlines;
    }

    public DomContent render() {
        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        List<DomContent> territoryHexes = outlines != null ? new ArrayList<>(outlines) : new ArrayList<>();

        AtomicReference<Hex> lowest = new AtomicReference<>(null);

        groupArea.forEach(hex -> {
            Point2D.Double coords = hex.toPixel();
            if (lowest.get() == null || coords.y > lowest.get().toPixel().y)
                lowest.set(hex);
        });

        Point2D.Double anchor = lowest.get().toPixel();

        territoryHexes.add(
                SvgTagCreator.text(group.getIdentifier())
                        .attr("x", anchor.x)
                        .attr("y", (int) (anchor.y + 25))
                        .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                        .attr("text-anchor", "middle")
                        .attr("class", "groupLabel")
        );

        String fqi = group.getFullyQualifiedIdentifier().jsonValue();
        return SvgTagCreator.g(territoryHexes.toArray(DomContent[]::new))
                .attr("id", fqi)
                .attr("data-identifier", fqi)
                .attr("data-x", anchor.x)
                .attr("data-y", anchor.y)
                .attr("class", "groupArea");
    }
}

