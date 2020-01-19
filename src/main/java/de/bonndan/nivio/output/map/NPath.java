package de.bonndan.nivio.output.map;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGPathSupport;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.svg.SVGPoint;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static de.bonndan.nivio.output.map.SvgTagCreator.g;

class NPath extends Component {

    private final TilePath tilePath;
    private final Layout layout;
    private final String fill;
    private final ItemMapItem.Relation relation;

    NPath(TilePath tilePath, Layout layout, String fill, ItemMapItem.Relation relation) {
        this.tilePath = tilePath;
        this.layout = layout;
        this.fill = fill;
        this.relation = relation;
    }

    public DomContent render() {

        var fillId = (fill) != null ? "#" + fill : null;
        var stringPath = tilePath.getPoints(layout);

        if ("PROVIDER".equals(relation.type)) {
            return SvgTagCreator.path().attr("d", stringPath).attr("stroke", fillId);
        }

        List<ContainerTag> markers = new ArrayList<>();
        BezierPath bezierPath = new BezierPath();
        bezierPath.parsePathString(stringPath);
        float pieces = bezierPath.path.curveLength / 20;
        float pct = 100 / pieces;

        for (float i = 0; i < 1; i += pct/100) {
            Point2D.Float point1 = bezierPath.eval(i);
            Point2D.Float point2 = bezierPath.eval(i+0.01f);
            markers.add(this.marker(point1, point2,  fillId));
        }

        return g(markers.toArray(DomContent[]::new));
    }

    private ContainerTag marker(Point2D.Float point, Point2D.Float point2, String fillId) {

        var degrees = Math.atan2( (point2.y - point.y), (point2.x - point.x) ) * 180 / Math.PI;
        String transform = "translate(" + Math.floor(point.getX()) + ' ' + point.getY() + ") rotate(" + degrees + " 0 0)";

        return SvgTagCreator.text("▸")
                .attr("x", "-10")
                .attr("y", 0)
                .attr("fill", fillId)
                .attr("width", 10)
                .attr("height", 10)
                .attr("transform", transform);
    }

    private List<Point2D.Float> getPoints(String s, int separate_points) {

        BezierPath bezierPath = new BezierPath();
        bezierPath.parsePathString(s);

        List<Point2D.Float> points = new ArrayList<>();
        for (float i = 0; i < 1; i += 0.1) {
            Point2D.Float eval = bezierPath.eval(i);
            points.add(eval);
        }

        return points;
    }
}

