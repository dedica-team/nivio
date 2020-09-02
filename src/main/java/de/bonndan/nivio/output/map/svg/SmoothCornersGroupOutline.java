package de.bonndan.nivio.output.map.svg;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class SmoothCornersGroupOutline {

    public static final int SMOOTHING_DIVISOR = 20;

    public static String getPath(ArrayList<Point2D.Double> corners) {

        ArrayList<Point2D.Double> bezierPoints = new ArrayList<>();

        for (int i = 0; i < corners.size(); i++) {
            int h = (i == 0) ? corners.size() - 1 : i - 1;
            int j = (i == corners.size() - 1) ? 0 : i + 1;
            Point2D.Double prev = corners.get(h);
            Point2D.Double point = corners.get(i);
            Point2D.Double following = corners.get(j);

            int f = SMOOTHING_DIVISOR;
            bezierPoints.add(
                    new Point2D.Double(
                            point.x + (prev.x - point.x) / f,
                            point.y + (prev.y - point.y) / f
                    )
            );

            bezierPoints.add(
                    new Point2D.Double(
                            point.x,
                            point.y
                    )
            );

            bezierPoints.add(
                    new Point2D.Double(
                            point.x + (following.x - point.x) / f,
                            point.y + (following.y - point.y) / f
                    )
            );
        }

        StringBuilder points = new StringBuilder("");
        for (var i = 0; i < bezierPoints.size(); i = i + 3) {

            var prev = bezierPoints.get(i);
            var controlPoint = bezierPoints.get(i + 1);
            var next = bezierPoints.get(i + 2);

            points.append(i == 0 ? "M " : "L ").append(prev.x).append(",").append(prev.y).append(" ");
            points.append(" Q ").append(controlPoint.x).append(",").append(controlPoint.y);
            points.append(" ").append(next.x).append(",").append(next.y);
        }
        points.append("L ").append(bezierPoints.get(0).x).append(",").append(bezierPoints.get(0).y); //closing
        return points.toString();
    }
}
