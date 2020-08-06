package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.*;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.bonndan.nivio.output.map.svg.SvgTagCreator.g;

/**
 * SVG representation of a relation between items.
 */
class SVGRelation extends Component {

    public static final String MARKER = "▸";
    private final HexPath hexPath;
    private final String fill;
    private final RelationItem<Item> relation;

    /**
     * @param hexPath the calculated best path
     * @param fill color
     * @param relation graph edge, source is the item this relation belongs to
     */
    SVGRelation(@NonNull HexPath hexPath, @NonNull String fill, @NonNull RelationItem<Item> relation) {
        this.hexPath = hexPath;
        Objects.requireNonNull(fill);
        this.fill = fill;
        this.relation = relation;
    }

    public DomContent render() {

        var fillId = (fill) != null ? "#" + fill : "";
        var stringPath = hexPath.getPoints();
        boolean isPlanned = Lifecycle.isPlanned(relation.getSource()) || Lifecycle.isPlanned(relation.getTarget());

        BezierPath bezierPath = new BezierPath();
        bezierPath.parsePathString(stringPath);

        if (RelationType.PROVIDER.equals(relation.getType())) {
            ContainerTag path = SvgTagCreator.path()
                    .attr("d", stringPath)
                    .attr("stroke", fillId);
            if (isPlanned) {
                path.attr("stroke-dasharray", 10);
                path.attr("opacity", 0.7);
            }
            return addAttributes(g(path, label(bezierPath, fillId)), relation);
        }

        List<ContainerTag> markers = new ArrayList<>();
        float pieces = bezierPath.path.curveLength / 20;
        float pct = 100 / pieces;
        for (float i = 0; i < 1; i += pct / 100) {
            Point2D.Float point1 = bezierPath.eval(i);
            Point2D.Float point2 = bezierPath.eval(i + 0.001f);
            markers.add(this.marker(point1, point2, fillId));
        }

        return addAttributes(
                g(markers.toArray(DomContent[]::new)),
                relation
        );
    }

    private ContainerTag addAttributes(ContainerTag g, RelationItem<Item> relation) {
        String type = !StringUtils.isEmpty(relation.getType()) ? relation.getType().name() : "-";
        g.attr("data-type", type)
                .attr("data-source", relation.getSource().getFullyQualifiedIdentifier().jsonValue())
                .attr("data-target", relation.getTarget().getFullyQualifiedIdentifier().jsonValue());

        return g;
    }

    private ContainerTag marker(Point2D.Float point, Point2D.Float point2, String fillId) {
        return alongPath(MARKER, point, point2, fillId, -10, false);
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
                .condAttr(!StringUtils.isEmpty(fillId), "fill", fillId)
                .attr("transform", transform);
    }

    public RelationItem<Item> getRelationItem() {
        return relation;
    }

    public HexPath getHexPath() {
        return hexPath;
    }
}

