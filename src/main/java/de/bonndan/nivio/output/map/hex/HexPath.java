package de.bonndan.nivio.output.map.hex;

import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Produces a point path along the centers of the given hexes.
 */
public class HexPath {

    private final List<PathTile> pathTiles;
    private final List<String> points = new ArrayList<>();
    private final Point2D.Double endPoint;
    private final List<Integer> directions;
    private int portCount;

    /**
     * @param pathTiles the hex tile chain in correct order.
     */
    public HexPath(@NonNull final List<PathTile> pathTiles) {
        this.pathTiles = Objects.requireNonNull(pathTiles);
        if (pathTiles.size() < 2) {
            throw new IllegalArgumentException("Paths cannot consist of only one tile");
        }
        directions = pathTiles.stream().map(PathTile::getDirectionFromParent).filter(Objects::nonNull).collect(Collectors.toList());
        if (!directions.isEmpty()) {
            directions.add(directions.get(directions.size()-1)); //copy last
        }
        this.endPoint = calcPoints();
    }

    /**
     * @return the endpoint of a list of strings forming a path of cubic curves
     */
    private Point2D.Double calcPoints() {
        points.add("M");

        for (var i = 0; i < pathTiles.size(); i++) {
            var pathTile = pathTiles.get(i);
            var mapTile = pathTile.getMapTile();
            var prevTile = i > 0 ? pathTiles.get(i - 1) : pathTile;//i==0 does not matter
            var prev = prevTile.getMapTile().getHex().toPixel();

            //the last segment needs to have half the length, so we can draw an endpoint marker there
            boolean isLast = i == pathTiles.size() - 1;
            var newBefore = new Point2D.Double();
            var point = mapTile.getHex().toPixel();
            var nextTile = isLast ? null : pathTiles.get(i + 1);
            if (!isLast && this.isBend(pathTile, nextTile)) {
                //cubic curve
                var next = nextTile.getMapTile().getHex().toPixel();

                newBefore.x = prev.x + (point.x - prev.x) / 2;
                newBefore.y = prev.y + (point.y - prev.y) / 2;

                points.addAll(List.of(" ", String.valueOf(newBefore.x), ",", String.valueOf(newBefore.y), " "));
                points.addAll(List.of("Q ", String.valueOf(point.x), ",", String.valueOf(point.y), " "));

                var newAfter = new Point2D.Double();
                newAfter.x = next.x + (point.x - next.x) / 2;
                newAfter.y = next.y + (point.y - next.y) / 2;

                points.addAll(List.of(String.valueOf(newAfter.x), ",", String.valueOf(newAfter.y), " L"));

            } else {
                if (isLast) {
                    var newAfter = new Point2D.Double();
                    //2.1 to prevent that the same point is hit as above (results in broken dataflow markers)
                    newAfter.x = point.x - (point.x - prev.x) / 2.1;
                    newAfter.y = point.y - (point.y - prev.y) / 2.1;
                    points.addAll(List.of(" ", String.valueOf(newAfter.x), ",", String.valueOf(newAfter.y)));
                    return new Point2D.Double(newAfter.x, newAfter.y);
                }

                points.addAll(List.of(" ", String.valueOf(point.x), ",", String.valueOf(point.y), " L"));
            }
        }
        return null;
    }

    /**
     * Returns all tiles which are part of the path.
     *
     */
    public List<PathTile> getTiles() {
        return Collections.unmodifiableList(pathTiles);
    }

    /**
     * Returns the path as svg path description with bezier curves.
     *
     * @return M...L notation
     */
    public List<String> getPoints() {
        return points;
    }

    /**
     * Returns the endpoint coordinates
     */
    public Point2D.Double getEndPoint() {
        return endPoint;
    }

    private boolean isBend(PathTile current, PathTile prevTile) {
        return current.getDirectionFromParent() != null && !current.getDirectionFromParent().equals(prevTile.getDirectionFromParent());
    }

    /**
     * Returns the bends from a list of adjacent hexes (a chain).
     *
     *
    void calcBends() {

        bends = new ArrayList<>();
        //bends
        IntStream.range(1, pathTiles.size() - 1).forEach(i -> {
            var prev = pathTiles.get(i - 1).getMapTile().getHex();
            var cur = pathTiles.get(i).getMapTile().getHex();
            var next = pathTiles.get(i + 1).getMapTile().getHex();
            var qBend = (prev.q == cur.q && next.q != cur.q) || (prev.q != cur.q && next.q == cur.q);
            var rBend = (prev.r == cur.r && next.r != cur.r) || (prev.r != cur.r && next.r == cur.r);
            if (qBend || rBend) {
                bends.add(cur);
            }
        });
    }
     */

    public List<Integer> getDirections() {
        return directions;
    }

    void setPortCount(int portCount) {
        this.portCount = portCount;
    }

    public int getPortCount() {
        return portCount;
    }
}
