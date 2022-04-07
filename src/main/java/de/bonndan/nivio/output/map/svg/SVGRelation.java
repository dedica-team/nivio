package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Label;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.output.map.hex.HexPath;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.bonndan.nivio.output.map.svg.SVGDocument.DATA_IDENTIFIER;
import static de.bonndan.nivio.output.map.svg.SVGDocument.VISUAL_FOCUS_UNSELECTED;
import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;
import static de.bonndan.nivio.output.map.svg.SvgTagCreator.g;

/**
 * SVG representation of a relation between items.
 */
class SVGRelation extends Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(SVGRelation.class);

    public static final String MARKER_ID = "arrow";
    public static final int BASIC_STROKE_WIDTH = 20;

    private final HexPath hexPath;
    private final String fill;
    private final Relation relation;

    @Nullable
    private final StatusValue statusValue;

    private Point2D.Double offset = new Point2D.Double(0, 0);

    /**
     * @param hexPath     the calculated best path
     * @param fill        color
     * @param relation    graph edge, source is the item this relation belongs to
     * @param statusValue status (worst) of source
     */
    SVGRelation(@NonNull final HexPath hexPath,
                @NonNull final String fill,
                @NonNull final Relation relation,
                @Nullable final StatusValue statusValue
    ) {
        this.hexPath = hexPath;
        if (!StringUtils.hasLength(fill)) {
            throw new IllegalArgumentException("Fill color cannot be empty.");
        }
        this.fill = fill;
        this.relation = relation;
        this.statusValue = statusValue;
    }

    @Override
    protected void applyShift(Point2D.Double offset) {
        this.offset = offset;
    }

    @Override
    public DomContent render() {

        final var fillId = "#" + fill;
        final var points = hexPath.getPoints().stream()
                .map(pathElement -> pathElement.shifted(offset))
                .collect(Collectors.joining(""));

        final float factor = Optional.ofNullable(relation.getLabel(Label.weight)).map(s -> {
            try {
                float v = Float.parseFloat(s);
                if (v > 5f) {
                    v = 5;
                }
                return v;
            } catch (NumberFormatException e) {
                LOGGER.warn("Invalid weight: {}", s);
                return 1f;
            }
        }).orElse(1f);
        final int innerStrokeWidth = Math.round(5 * factor);
        final int yPortTranslation = getTranslation();

        //the bezier path is used to interpolate the "stringPath" in order to find the position for the label
        final BezierPath bezierPath = new BezierPath();
        bezierPath.parsePathString(points);

        final var endPoint = bezierPath.angleAtEnd(
                SvgRelationEndMarker.HALF_MARKER_SIZE,
                0,
                yPortTranslation
        );

        ContainerTag path = SvgTagCreator.path()
                .attr("d", points)
                .attr(SVGAttr.STROKE, fillId)
                .attr(SVGAttr.STROKE_WIDTH, innerStrokeWidth)
                .attr(SVGAttr.TRANSFORM, String.format("translate(0 %d)", yPortTranslation));

        if (Lifecycle.isPlanned(relation.getSource()) || Lifecycle.isPlanned(relation.getTarget())) {
            path.attr("opacity", "0.5");
        }

        if (RelationType.DATAFLOW.name().equals(relation.getType())) {
            path.attr(SVGAttr.FILL, fillId);
            path.attr("stroke-dasharray", 15);
        }

        SvgRelationEndMarker marker = new SvgRelationEndMarker(
                endPoint,
                RelationType.from(relation.getType()),
                fillId
        );
        ContainerTag endMarker = marker.render();

        ContainerTag label = createLabel(relation.getLabel(Label.label), bezierPath, fillId, statusValue, yPortTranslation);

        return addAttributes(g(path, endMarker, label), relation);
    }

    private int getTranslation() {
        var total = hexPath.getTotalPortCount();
        if (total < 2) return 0;

        float factor = (hexPath.getPortCount() - 1f) / total * 20;
        int dir = hexPath.getPortCount() % 2 > 0 ? 1 : -1;
        return Math.round(factor * dir);
    }

    public HexPath getHexPath() {
        return hexPath;
    }

    private ContainerTag addAttributes(ContainerTag g, Relation relation) {
        String type = relation.getType() != null ? relation.getType() : "-";
        g.attr("data-type", type)
                .attr("data-source", relation.getSource().getFullyQualifiedIdentifier().toString())
                .attr("data-target", relation.getTarget().getFullyQualifiedIdentifier().toString())
                .attr(DATA_IDENTIFIER, relation.getFullyQualifiedIdentifier())
                .attr(SVGAttr.CLASS, String.format("relation %s", VISUAL_FOCUS_UNSELECTED));

        return g;
    }

    @Nullable
    private ContainerTag createLabel(String text,
                                     BezierPath bezierPath,
                                     String fillId,
                                     StatusValue statusValue,
                                     int translation
    ) {
        return new SvgRelationLabel(
                text,
                bezierPath.angleAt(0.49f, 0.51f, 0, translation, true),
                fillId,
                statusValue
        ).render();
    }

    public DomContent renderAsProcessBranch(@NonNull final String processColor) {

        final var points = hexPath.getPoints().stream()
                .map(pathElement -> pathElement.shifted(offset))
                .collect(Collectors.joining(""));

        //the bezier path is used to interpolate the "stringPath" in order to find the position for the label
        final BezierPath bezierPath = new BezierPath();
        bezierPath.parsePathString(points);

        final var endPoint = bezierPath.angleAtEnd(
                SvgRelationEndMarker.HALF_MARKER_SIZE * 2,
                0,
                0
        );

        ContainerTag shadow = SvgTagCreator.path()
                .attr("d", points)
                .attr(SVGAttr.STROKE, processColor)
                .attr(SVGAttr.FILL, "transparent")
                .attr(SVGAttr.STROKE_WIDTH, (BASIC_STROKE_WIDTH * 2));

        ContainerTag shadowMarker = new SvgRelationEndMarker(
                endPoint,
                null,
                processColor,
                3
        ).render();


        return g(shadow, shadowMarker);
    }

    /**
     * Create a group-colored marker.
     *
     * @return a reusable marker for data flow paths
     */
    public static ContainerTag dataflowMarker() {
        ContainerTag path = SvgTagCreator.path().attr("d", "M 0 0 L 10 5 L 0 10 z")
                .attr("fill", "grey");

        return SvgTagCreator.marker()
                .attr("id", MARKER_ID)
                .attr("markerWidth", DEFAULT_ICON_SIZE)
                .attr("markerHeight", DEFAULT_ICON_SIZE)
                .attr("refX", 14)
                .attr("refY", 5)
                .attr("orient", "auto")
                .attr("viewBox", "0 0 10 10")
                .attr(SVGAttr.STROKE, "context-stroke")
                .attr("markerUnits", "userSpaceOnUse")
                .with(path)
                ;
    }


}

