package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.Group;
import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.MapTile;
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
import java.util.stream.Collectors;

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
    private final Set<MapTile> groupArea;

    @Nullable
    private final List<Component> components;

    @NonNull
    private final Point2D.Double anchor;

    private Point2D.Double offset = new Point2D.Double(0, 0);

    /**
     * Builds an areas of hex tiles belonging to a group.
     *
     * @param group  the group
     * @param inArea all hex tiles forming an area
     * @param debug  turn on debugging
     */
    public static SVGGroupArea forGroup(@NonNull final Group group,
                                        @NonNull final Set<MapTile> inArea,
                                        boolean debug
    ) {
        var fill = Objects.requireNonNull(group).getColor();
        var fillId = fill != null ? "#" + fill : "";

        HexGroupAreaOutlineFactory outlineFactory = new HexGroupAreaOutlineFactory();
        outlineFactory.setDebug(debug);

        List<Component> outlines = outlineFactory.getOutline(inArea, fillId);
        return new SVGGroupArea(group, inArea, outlines);
    }

    SVGGroupArea(@NonNull final Group group,
                 @NonNull final Set<MapTile> groupArea,
                 @NonNull final List<Component> components
    ) {
        this.group = Objects.requireNonNull(group);
        this.groupArea = Objects.requireNonNull(groupArea);
        this.components = components;

        AtomicReference<Hex> lowest = new AtomicReference<>(null);
        groupArea.forEach(tile -> {
            Point2D.Double coords = tile.getHex().toPixel();
            if (lowest.get() == null || coords.y > lowest.get().toPixel().y)
                lowest.set(tile.getHex());
        });
        anchor = lowest.get() != null ? lowest.get().toPixel() : new Point2D.Double(0, 0);
    }

    @Override
    protected void applyShift(Point2D.Double offset) {
        anchor.x = anchor.x + offset.getX();
        anchor.y = anchor.y + offset.getY();
        this.offset = offset;
    }

    @Override
    public DomContent render() {
        List<Component> territoryHexes = components != null ? new ArrayList<>(components) : new ArrayList<>();
        String fqi = group.getFullyQualifiedIdentifier().toString();
        if (!StringUtils.hasLength(fqi)) {
            // we can still render an svg, but area will not be clickable
            LOGGER.warn("Empty group fqi in SVG group area, group {}", group);
        }

        List<DomContent> domContents = territoryHexes.stream()
                .map(component -> component.shift(offset).render())
                .collect(Collectors.toList());
        return SvgTagCreator.g(domContents.toArray(DomContent[]::new))
                .with(getLabel())
                .attr("id", fqi)
                .attr(DATA_IDENTIFIER, fqi)
                .attr("data-x", anchor.x)
                .attr("data-y", anchor.y)
                .attr(SVGAttr.CLASS, "groupArea " + VISUAL_FOCUS_UNSELECTED);
    }

    /**
     * Creates the group label text element positioned at the anchor (lowest point).
     */
    @NonNull
    public ContainerTag getLabel() {

        var x = anchor.x;
        var y = anchor.y + SVGRenderer.DEFAULT_ICON_SIZE * 2;
        var fontSize = 8;
        boolean small = group.getChildren().size() < 4;
        if (!small) {
            fontSize = 16;
            //calculate center
            SVGDimension dimension = SVGDimensionFactory.getDimension(List.of(this), Collections.emptyList());
            x = dimension.cartesian.horMin + (dimension.cartesian.horMax - dimension.cartesian.horMin) / 2f + offset.x;
            y = dimension.cartesian.vertMin + (dimension.cartesian.vertMax - dimension.cartesian.vertMin) / 2f + offset.y;
            y += SVGRenderer.DEFAULT_ICON_SIZE;
        }

        var fill = group.getColor();
        var fillId = fill != null ? "#" + fill : "";

        return SvgTagCreator.text(group.getIdentifier())
                .attr("x", x)
                .attr("y", y)
                .attr(SVGAttr.FILL, fillId)
                .attr("text-anchor", "middle")
                .attr("style", "text-shadow: 1px 2px 2px black")
                .attr("font-size", fontSize + "em")
                .attr(SVGAttr.CLASS, "groupLabel");
    }

    @NonNull
    public Set<MapTile> getGroupArea() {
        return Collections.unmodifiableSet(groupArea);
    }
}

