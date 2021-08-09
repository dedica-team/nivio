package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Produces a point path along the centers of the given hexes.
 */
public class HexPath {

    private final List<Hex> hexes;
    private final List<String> points = new ArrayList<>();
    private List<Hex> bends;
    private List<Integer> directions;
    private final Point2D.Double endPoint;

    /**
     * @param hexes the hex tile chain in correct order.
     */
    public HexPath(final List<Hex> hexes) {
        this.hexes = hexes;
        calcBends(hexes);
        this.endPoint = calcPoints();
    }

    /**
     * @return a list of strings forming a path of cubic curves
     */
    private Point2D.Double calcPoints() {
        points.add("M");

        for (var i = 0; i < hexes.size(); i++) {
            var hex = hexes.get(i);
            var prev = i > 0 ? hexes.get(i - 1).toPixel() : hex.toPixel(); //i==0 does not matter

            //the last segment needs to have half the length so we can draw an endpoint marker there
            boolean isLast = i == hexes.size() - 1;
            var newBefore = new Point2D.Double();
            var point = hex.toPixel();
            if (this.isBend(hex)) {
                //cubic curve
                var next = hexes.get(i + 1).toPixel();

                newBefore.x = prev.x + (point.x - prev.x) / 2;
                newBefore.y = prev.y + (point.y - prev.y) / 2;

                points.addAll(List.of(" ", String.valueOf(newBefore.x), ",", String.valueOf(newBefore.y), " "));
                points.addAll(List.of("Q ", String.valueOf(point.x), ",", String.valueOf(point.y), " "));

                var newAfter = new Point2D.Double();
                newAfter.x = next.x + (point.x - next.x) / 2;
                newAfter.y = next.y + (point.y - next.y) / 2;

                if (isLast) {
                    newAfter.x = next.x + (point.x - next.x) / 10;
                    newAfter.y = next.y + (point.y - next.y) / 10;
                    points.addAll(List.of(String.valueOf(newAfter.x), ",", String.valueOf(newAfter.y)));
                    return new Point2D.Double(point.x, point.y);
                } else {
                    points.addAll(List.of(String.valueOf(newAfter.x), ",", String.valueOf(newAfter.y), " L"));
                }
            } else {
                if (isLast) {
                    var newAfter = new Point2D.Double();
                    newAfter.x = point.x - (point.x - prev.x) / 2;
                    newAfter.y = point.y - (point.y - prev.y) / 2;
                    points.addAll(List.of(" ", String.valueOf(newAfter.x), ",", String.valueOf(newAfter.y)));
                    return new Point2D.Double(newAfter.x, newAfter.y);
                } else {
                    points.addAll(List.of(" ", String.valueOf(point.x), ",", String.valueOf(point.y), " L"));
                }
            }
        }
        return null;
    }

    /**
     * Returns all hex tiles which are part of the path.
     */
    public List<Hex> getHexes() {
        return Collections.unmodifiableList(hexes);
    }

    /**
     * Returns the path as svg path description with bezier curves.
     *
     * @return M...L notation
     */
    List<String> getPoints() {
        return points;
    }

    /**
     * Returns the endpoint coordinates
     */
    Point2D.Double getEndPoint() {
        return endPoint;
    }

    private boolean isBend(Hex hex) {

        for (Hex bend : this.bends) {
            if (bend.q == hex.q && bend.r == hex.r) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the bends from a list of adjacent hexes (a chain).
     *
     * @param hexes the hex chain
     */
    void calcBends(final List<Hex> hexes) {

        bends = new ArrayList<>();
        directions = new ArrayList<>();
        var i = 0;
        for (i = 1; i < hexes.size() - 1; i++) {
            var prev = hexes.get(i - 1);
            var cur = hexes.get(i);
            var next = hexes.get(i + 1);

            //bends
            var qBend = (prev.q == cur.q && next.q != cur.q) || (prev.q != cur.q && next.q == cur.q);
            var rBend = (prev.r == cur.r && next.r != cur.r) || (prev.r != cur.r && next.r == cur.r);
            if (qBend || rBend) {
                bends.add(cur);
            }

            //directions
            directions.add(prev.getDirectionTo(cur));

            if (i == hexes.size() - 2) {
                directions.add(cur.getDirectionTo(next));
            }
        }
    }

    public List<Hex> getBends() {
        return Collections.unmodifiableList(bends);
    }

    public List<Integer> getDirections() {
        return Collections.unmodifiableList(directions);
    }
}
