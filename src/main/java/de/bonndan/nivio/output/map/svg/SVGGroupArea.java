package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
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

import static de.bonndan.nivio.output.map.svg.SVGDocument.DATA_IDENTIFIER;
import static de.bonndan.nivio.output.map.svg.SVGDocument.VISUAL_FOCUS_UNSELECTED;


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
    private final StatusValue groupStatus;

    @NonNull
    private final Point2D.Double anchor;

    /**
     * Builds an areas of hex tiles belonging to a group.
     *
     * @param group       the group
     * @param inArea      all hex tiles forming an area
     * @param groupStatus assessment status summary of the group
     * @param debug       turn on debugging
     */
    public static SVGGroupArea forGroup(@NonNull final Group group,
                                        @NonNull final Set<Hex> inArea,
                                        @NonNull final StatusValue groupStatus,
                                        boolean debug
    ) {
        var fill = Objects.requireNonNull(group).getColor();
        var fillId = fill != null ? "#" + fill : "";

        SVGGroupAreaOutlineFactory outlineFactory = new SVGGroupAreaOutlineFactory(SVGGroupAreaOutlineFactory.GroupAreaStyle.HEXES);
        outlineFactory.setDebug(debug);

        List<DomContent> outlines = outlineFactory.getOutline(inArea, fillId);
        return new SVGGroupArea(group, inArea, outlines, groupStatus);
    }

    SVGGroupArea(@NonNull final Group group,
                 @NonNull final Set<Hex> groupArea,
                 @NonNull final List<DomContent> outlines,
                 @NonNull final StatusValue groupStatus
    ) {
        this.group = Objects.requireNonNull(group);
        this.groupArea = Objects.requireNonNull(groupArea);
        this.outlines = outlines;
        this.groupStatus = groupStatus;

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
                .with(getLabel())
                .attr("id", fqi)
                .attr(DATA_IDENTIFIER, fqi)
                .attr("data-x", anchor.x)
                .attr("data-y", anchor.y)
                .attr("class", "groupArea " + VISUAL_FOCUS_UNSELECTED);
    }

    /**
     * Creates the group label text element positioned at the anchor (lowest point).
     */
    @NonNull
    public ContainerTag getLabel() {

        var x = anchor.x;
        var y = anchor.y + SVGRenderer.DEFAULT_ICON_SIZE * 2;
        var fontSize = 8;
        boolean small = group.getItems().size() < 4;
        if (!small) {
            fontSize = 16;
            //calculate center
            SVGDimension dimension = SVGDimensionFactory.getDimension(List.of(this), Collections.emptyList());
            x = dimension.cartesian.horMin + (dimension.cartesian.horMax - dimension.cartesian.horMin) / 2f;
            y = dimension.cartesian.vertMin + (dimension.cartesian.vertMax - dimension.cartesian.vertMin) / 2f;
            y += SVGRenderer.DEFAULT_ICON_SIZE;
        }

        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        return SvgTagCreator.text(group.getIdentifier())
                .attr("x", x)
                .attr("y", y)
                .attr("fill", fillId)
                .attr("text-anchor", "middle")
                .attr("style", "text-shadow: 1px 2px 2px black")
                .attr("font-size", fontSize + "em")
                .attr("class", "groupLabel");
    }

    @NonNull
    public Set<Hex> getGroupArea() {
        return Collections.unmodifiableSet(groupArea);
    }
}

