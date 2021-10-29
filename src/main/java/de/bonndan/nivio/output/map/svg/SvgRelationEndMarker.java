package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.model.RelationType;
import de.bonndan.nivio.output.map.hex.Hex;
import j2html.tags.ContainerTag;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.awt.geom.Point2D;
import java.util.Objects;

import static de.bonndan.nivio.output.map.hex.Hex.*;

/**
 * Renders an {@link SVGRelation} end marker.
 */
class SvgRelationEndMarker {

    private static final String MARKER_POINTS = "25,5 48,50 5,50";
    private static final int HALF_MARKER_SIZE = 25;

    private final Point2D.Double endPoint;
    private final RelationType type;
    private final String fillId;
    private final int lastDirection;

    /**
     * @param endPoint      end point of the path
     * @param type          relation type
     * @param fillColor     color
     * @param lastDirection direction of the last path tile to determine rotation angle
     */
    SvgRelationEndMarker(@NonNull final Point2D.Double endPoint,
                         @Nullable final RelationType type,
                         @Nullable final String fillColor,
                         int lastDirection
    ) {
        this.endPoint = Objects.requireNonNull(endPoint);
        this.type = type;
        this.fillId = fillColor;
        this.lastDirection = lastDirection;
    }

    ContainerTag render() {
        if (RelationType.DATAFLOW.equals(type)) {

            var hexDirCorrection = lastDirection - Hex.NORTH;
            var degree = hexDirCorrection * 60;
            var offset = offset(lastDirection);
            double tx = endPoint.x - HALF_MARKER_SIZE + offset.x;
            double ty = endPoint.y - HALF_MARKER_SIZE + offset.y;
            return SvgTagCreator.polygon()
                    .attr("transform", String.format("translate(%s %s) rotate(%d 25 25)", tx, ty, degree))
                    .attr("points", MARKER_POINTS)
                    .attr("fill", fillId);
        } else {
            return SvgTagCreator.circle()
                    .attr("cx", endPoint.x)
                    .attr("cy", endPoint.y)
                    .attr("r", 35)
                    .attr("fill", fillId);
        }
    }

    /**
     * @param degree rotation degree
     * @return the point of the tip
     */
    private Point2D.Double offset(int degree) {

        switch (lastDirection) {

            case SOUTH:
                degree = 0;
                break;

            case SOUTH_WEST:
                degree = 60;
                break;

            case NORTH_WEST:
                degree = 120;
                break;

            case NORTH:
                degree = 180;
                break;
            case NORTH_EAST:
                degree = 240;
                break;

            case SOUTH_EAST:
                degree = 300;
                break;
        }

        var radius = HALF_MARKER_SIZE;
        var t = Math.toRadians(degree);
        int x = (int) Math.round(Math.sin(t) * radius);
        int y = (int) Math.round(Math.cos(t) * radius);

        return new Point2D.Double(x, -y);
    }
}
