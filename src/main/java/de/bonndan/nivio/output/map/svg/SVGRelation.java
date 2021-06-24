package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.assessment.Status;
import de.bonndan.nivio.assessment.StatusValue;
import de.bonndan.nivio.model.Lifecycle;
import de.bonndan.nivio.model.Relation;
import de.bonndan.nivio.model.RelationType;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Null;
import java.awt.geom.Point2D;
import java.util.Optional;

import static de.bonndan.nivio.output.map.svg.SvgTagCreator.g;

/**
 * SVG representation of a relation between items.
 */
class SVGRelation extends Component {

    public static final String MARKER_ID = "arrow";

    private final HexPath hexPath;
    private final String fill;
    private final Relation relation;
    private final StatusValue statusValue;

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
        if (StringUtils.isEmpty(fill)) {
            throw new RuntimeException("Fill color cannot be empty.");
        }
        this.fill = fill;
        this.relation = relation;
        this.statusValue = statusValue;
    }

    @Override
    public DomContent render() {

        var fillId = "#" + fill;

        //the bezier path is only used to interpolate the "stringPath"
        BezierPath bezierPath = new BezierPath();
        var points = String.join("", hexPath.getPoints());
        bezierPath.parsePathString(points);

        String statusColor = statusValue.getStatus().getName();

        ContainerTag shadow = null;
        int innerStrokeWidth = 5;
        if (statusValue.getStatus().equals(Status.UNKNOWN)) {
            innerStrokeWidth = 15;
        } else {
            shadow = SvgTagCreator.path()
                    .attr("d", points)
                    .attr("stroke", statusColor)
                    .attr("stroke-width", 20);
        }

        ContainerTag path = SvgTagCreator.path()
                .attr("d", points)
                .attr("stroke", fillId)
                .attr("stroke-width", innerStrokeWidth);



        if (Lifecycle.isPlanned(relation.getSource()) || Lifecycle.isPlanned(relation.getTarget())) {
            path.attr("stroke-dasharray", 15);
        }

        ContainerTag endMarker = null;
        if (RelationType.DATAFLOW.equals(relation.getType())) {
            path.attr("marker-mid", String.format("url(#%s)", SVGRelation.MARKER_ID));
            path.attr("fill", shadow != null ? statusColor: fillId);
        } else {
            endMarker = SvgTagCreator.circle()
                    .attr("cx", hexPath.getEndPoint().x)
                    .attr("cy", hexPath.getEndPoint().y)
                    .attr("r", 35)
                    .attr("fill", shadow != null ? statusColor: fillId);
        }

        return addAttributes(g(shadow, endMarker, path, label(bezierPath, fillId)), relation);
    }

    public HexPath getHexPath() {
        return hexPath;
    }

    private ContainerTag addAttributes(ContainerTag g, Relation relation) {
        String type = !StringUtils.isEmpty(relation.getType()) ? relation.getType().name() : "-";
        g.attr("data-type", type)
                .attr("data-source", relation.getSource().getFullyQualifiedIdentifier().jsonValue())
                .attr("data-target", relation.getTarget().getFullyQualifiedIdentifier().jsonValue())
                .attr("class", "relation");

        return g;
    }

    private ContainerTag label(BezierPath bezierPath, String fillId) {
        Point2D.Float point = bezierPath.eval(0.49f);
        Point2D.Float point2 = bezierPath.eval(0.51f);
        return alongPath(getText(), point, point2, fillId, 0, true);
    }

    private String getText() {
        return Optional.ofNullable(relation.getFormat()).orElse("");
    }

    private ContainerTag alongPath(String text, Point2D.Float point, Point2D.Float point2, String fillId, int xOffset, boolean upright) {

        var degrees = Math.atan2((point2.y - point.y), (point2.x - point.x)) * 180 / Math.PI;
        if (upright && (degrees > 90 || degrees < -90)) {
            degrees += 180; //always upright
        }
        String transform = "translate(" + round(point.getX()) + ' ' + round(point.getY() - 10) + ") rotate(" + round(degrees) + " 0 0)";

        if (text == null) {
            text = "";
        }
        return SvgTagCreator.text(text)
                .attr("x", xOffset)
                .attr("y", 0)
                .attr("font-size", "4em")
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .attr("transform", transform);
    }

    /**
     * Create a group-colored marker.
     *
     * @return a reusable marker for data flow paths
     */
    public static ContainerTag dataflowMarker() {
        ContainerTag path = SvgTagCreator.path().attr("d", "M 0 0 L 10 5 L 0 10 z")
                .attr("fill", "#ffffff");

        return SvgTagCreator.marker()
                .attr("id", MARKER_ID)
                .attr("markerWidth", 10)
                .attr("markerHeight", 10)
                .attr("refX", 0)
                .attr("refY", 5)
                .attr("orient", "auto")
                .attr("viewBox", "0 0 10 10")
                .attr("stroke", "context-stroke")
                .attr("markerUnits", "userSpaceOnUse")
                .with(path)
                ;
    }
}

