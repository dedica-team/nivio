package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Displays a group as an area containing items.
 */
class SVGGroupArea extends Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGGroupArea.class);

    @NonNull
    private final Group group;

    @NonNull
    private final Set<Hex> groupArea;

    @Nullable
    private final List<DomContent> outlines;

    @NonNull
    private final Point2D.Double anchor;

    SVGGroupArea(@NonNull final Group group,
                 @NonNull final Set<Hex> groupArea,
                 @NonNull final List<DomContent> outlines
    ) {
        this.group = Objects.requireNonNull(group);
        this.groupArea = Objects.requireNonNull(groupArea);
        this.outlines = outlines;

        AtomicReference<Hex> lowest = new AtomicReference<>(null);
        groupArea.forEach(hex -> {
            Point2D.Double coords = hex.toPixel();
            if (lowest.get() == null || coords.y > lowest.get().toPixel().y)
                lowest.set(hex);
        });
        anchor = lowest.get() != null ? lowest.get().toPixel() : new Point2D.Double(0, 0);

    }

    @Override
    public DomContent render() {
        List<DomContent> territoryHexes = outlines != null ? new ArrayList<>(outlines) : new ArrayList<>();
        String fqi = group.getFullyQualifiedIdentifier().jsonValue();
        if (StringUtils.isEmpty(fqi)) {
            // we can still render an svg, but area will not be clickable
            LOGGER.warn("Empty group fqi in SVG group area, group {}", group);
        }
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

    @NonNull
    public Set<Hex> getGroupArea() {
        return Collections.unmodifiableSet(groupArea);
    }
}

