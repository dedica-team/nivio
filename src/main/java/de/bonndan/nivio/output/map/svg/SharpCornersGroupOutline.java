package de.bonndan.nivio.output.map.svg;

import java.awt.geom.Point2D;
import java.util.List;

public class SharpCornersGroupOutline {

    public static String getPath(List<Point2D.Double> corners) {
        StringBuilder points = new StringBuilder("M");
        for (var i = 0; i < corners.size(); i++) {
            var point = corners.get(i);
            points.append(" ").append(point.x).append(",").append(point.y);
        }

        points.append(" ").append(corners.get(0).x).append(",").append(corners.get(0).y);
        return points.toString();
    }
}
