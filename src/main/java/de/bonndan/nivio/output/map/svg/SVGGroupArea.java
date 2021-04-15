package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
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
    private final Point2D.Double anchor;

    SVGGroupArea(@NonNull final Group group, @NonNull final Set<Hex> groupArea, @NonNull final List<DomContent> outlines) {
        this.group = group;
        this.groupArea = groupArea;
        this.outlines = outlines;


        AtomicReference<Hex> lowest = new AtomicReference<>(null);
        groupArea.forEach(hex -> {
            Point2D.Double coords = hex.toPixel();
            if (lowest.get() == null || coords.y > lowest.get().toPixel().y)
                lowest.set(hex);
        });
        anchor = lowest.get() != null ? lowest.get().toPixel() : new Point2D.Double(0, 0);
    }

    public DomContent render() {

        List<DomContent> territoryHexes = outlines != null ? new ArrayList<>(outlines) : new ArrayList<>();
        String fqi = group.getFullyQualifiedIdentifier().jsonValue();
        return SvgTagCreator.g(territoryHexes.toArray(DomContent[]::new))
                .attr("id", fqi)
                .attr("data-identifier", fqi)
                .attr("data-x", anchor.x)
                .attr("data-y", anchor.y)
                .attr("class", "groupArea");
    }

    /**
     * Creates the group label text element positioned at the anchor (lowest point).
     *
     */
    public ContainerTag getLabel() {
        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        return SvgTagCreator.text(group.getIdentifier())
                .attr("x", anchor.x)
                .attr("y", (int) (anchor.y + 25))
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .attr("text-anchor", "middle")
                .attr("class", "groupLabel");
    }
}

