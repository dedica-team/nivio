package de.bonndan.nivio.output.map.svg;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class WobblyGroupOutline {

    public static String getPath(List<Point2D.Double> corners) {
        ArrayList<Point2D.Double> middles = new ArrayList<>();

        for (int i = 0; i < corners.size(); i++) {
            int j = (i == corners.size() - 1) ? 0 : i + 1;
            Point2D.Double point = corners.get(i);
            Point2D.Double following = corners.get(j);
            middles.add(
                    new Point2D.Double(
                            point.x + (following.x - point.x) / 2,
                            point.y + (following.y - point.y) / 2
                    )
            );
        }

        StringBuilder points = new StringBuilder("M");
        for (var i = 0; i < middles.size(); i++) {

            var j = i+1;
            if (i == middles.size() - 1) //when the last ist reached, reconnect to first
                j = 0;

            //cubic curve, original points are now the control points
            var prev = middles.get(i);
            var controlPoint = corners.get(j);
            var next = middles.get(j);

            points.append(" ").append(prev.x).append(",").append(prev.y).append(" ");
            points.append("Q ").append(controlPoint.x).append(",").append(controlPoint.y);

            if (j == 0) {
                points.append(" ").append(next.x).append(",").append(next.y);
            }

        }
        return points.toString();
    }
}
