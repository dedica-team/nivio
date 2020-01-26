package de.bonndan.nivio.output.map;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static de.bonndan.nivio.output.map.SvgTagCreator.g;

class NPath extends Component {

    public static final String MARKER = "▸";
    private final TilePath tilePath;
    private final String fill;
    private final ItemMapItem.Relation relation;

    NPath(TilePath tilePath, String fill, ItemMapItem.Relation relation) {
        this.tilePath = tilePath;
        this.fill = fill;
        this.relation = relation;
    }

    public DomContent render() {

        var fillId = (fill) != null ? "#" + fill : null;
        var stringPath = tilePath.getPoints();

        BezierPath bezierPath = new BezierPath();
        bezierPath.parsePathString(stringPath);

        if ("PROVIDER".equals(relation.type)) {
            return g(
                    SvgTagCreator.path()
                            .attr("d", stringPath)
                            .attr("stroke", fillId)
                            .attr("data-target", relation.target)
                            .attr("data-type", relation.type)
                    ,
                    label(bezierPath, fillId)
            );
        }

        List<ContainerTag> markers = new ArrayList<>();
        float pieces = bezierPath.path.curveLength / 20;
        float pct = 100 / pieces;
        for (float i = 0; i < 1; i += pct / 100) {
            Point2D.Float point1 = bezierPath.eval(i);
            Point2D.Float point2 = bezierPath.eval(i + 0.01f);
            markers.add(this.marker(point1, point2, fillId));
        }

        return g(markers.toArray(DomContent[]::new));
    }

    private ContainerTag marker(Point2D.Float point, Point2D.Float point2, String fillId) {
        return alongPath(MARKER, point, point2, fillId, -10);
    }

    private ContainerTag label(BezierPath bezierPath, String fillId) {
        Point2D.Float point = bezierPath.eval(0.5f);
        Point2D.Float point2 = bezierPath.eval(0.51f);
        return alongPath(relation.format, point, point2, fillId, 10);
    }

    private ContainerTag alongPath(String text, Point2D.Float point, Point2D.Float point2, String fillId, int xOffset) {

        var degrees = Math.atan2((point2.y - point.y), (point2.x - point.x)) * 180 / Math.PI;
        String transform = "translate(" + Math.floor(point.getX()) + ' ' + point.getY() + ") rotate(" + degrees + " 0 0)";

        if (text == null) {
            text = "";
        }
        return SvgTagCreator.text(text)
                .attr("x", xOffset)
                .attr("y", 0)
                .attr("fill", fillId)
                .attr("width", 10)
                .attr("height", 10)
                .attr("transform", transform);
    }
}

