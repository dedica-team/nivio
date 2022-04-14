package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.RelationType;
import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

import static java.lang.Math.round;

/**
 * Renders an {@link SVGRelation} end marker.
 */
class SvgRelationEndMarker {

    private static final String MARKER_POINTS = "25,5 48,50 5,50";
    public static final int HALF_MARKER_SIZE = 25;

    private final BezierPath.PointWithAngle endPoint;
    private final RelationType type;
    private final String fillId;
    private final int scale;

    /**
     * @param endPoint      end point of the path
     * @param type          relation type
     * @param fillColor     color
     */
    SvgRelationEndMarker(@NonNull final BezierPath.PointWithAngle endPoint,
                         @Nullable final RelationType type,
                         @Nullable final String fillColor
    ) {
        this.endPoint = Objects.requireNonNull(endPoint);
        this.type = type;
        this.fillId = fillColor;
        this.scale = 1;
    }

    /**
     * @param endPoint      end point of the path
     * @param type          relation type
     * @param fillColor     color
     */
    SvgRelationEndMarker(@NonNull final BezierPath.PointWithAngle endPoint,
                         @Nullable final RelationType type,
                         @Nullable final String fillColor,
                         int scale
    ) {
        this.endPoint = Objects.requireNonNull(endPoint);
        this.type = type;
        this.fillId = fillColor;
        this.scale = scale;
    }

    ContainerTag render() {
        if (RelationType.PROVIDER.equals(type)) {
            return SvgTagCreator.circle()
                    .attr("cx", endPoint.point.x)
                    .attr("cy", endPoint.point.y)
                    .attr("r", 35)
                    .attr(SVGAttr.FILL, fillId);
        }

        var degree = (float)endPoint.degrees + 90;
        final int scaledSize = HALF_MARKER_SIZE * scale;
        double tx = endPoint.point.x - scaledSize ;
        double ty = endPoint.point.y - scaledSize ;
        String transformation = String.format("translate(%s %s) rotate(%d %d %d)", tx, ty, round(degree), scaledSize, scaledSize);
        if (scale != 1) {
            transformation += String.format(" scale(%s,%s)", scale, scale);
        }
        return SvgTagCreator.polygon()
                .attr(SVGAttr.TRANSFORM, transformation)
                .attr(SVGAttr.POINTS, MARKER_POINTS)
                .attr(SVGAttr.FILL, fillId);
    }
}
