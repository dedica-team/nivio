package de.bonndan.nivio.output.map.svg;

import de.bonndan.nivio.output.map.hex.Hex;
import de.bonndan.nivio.output.map.hex.MapTile;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Produces a point path along the centers of the given hexes.
 */
public class HexPath {

    private final List<MapTile> mapTiles;
    private final List<String> points = new ArrayList<>();
    private List<Hex> bends;
    private final Point2D.Double endPoint;

    /**
     * @param mapTiles the hex tile chain in correct order.
     */
    public HexPath(@NonNull final List<MapTile> mapTiles) {
        this.mapTiles = Objects.requireNonNull(mapTiles);
        if (mapTiles.size() < 2) {
            throw new IllegalArgumentException("Paths cannot consist of only one tile");
        }
        calcBends();
        calcDirections(mapTiles);
        this.endPoint = calcPoints();
    }

    /**
     * @return the endpoint of a list of strings forming a path of cubic curves
     */
    private Point2D.Double calcPoints() {
        points.add("M");

        for (var i = 0; i < mapTiles.size(); i++) {
            var mapTile = mapTiles.get(i);
            var prev = i > 0 ? mapTiles.get(i - 1).getHex().toPixel() : mapTile.getHex().toPixel(); //i==0 does not matter

            //the last segment needs to have half the length so we can draw an endpoint marker there
            boolean isLast = i == mapTiles.size() - 1;
            var newBefore = new Point2D.Double();
            var point = mapTile.getHex().toPixel();
            if (this.isBend(mapTile)) {
                //cubic curve
                var next = mapTiles.get(i + 1).getHex().toPixel();

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
                }

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
    public List<MapTile> getMapTiles() {
        return Collections.unmodifiableList(mapTiles);
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

    private boolean isBend(MapTile mapTile) {

        for (Hex bend : this.bends) {
            if (bend.q == mapTile.getHex().q && bend.r == mapTile.getHex().r) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the bends from a list of adjacent hexes (a chain).
     *
     */
    void calcBends() {

        bends = new ArrayList<>();
        //bends
        IntStream.range(1, mapTiles.size() - 1).forEach(i -> {
            var prev = mapTiles.get(i - 1).getHex();
            var cur = mapTiles.get(i).getHex();
            var next = mapTiles.get(i + 1).getHex();
            var qBend = (prev.q == cur.q && next.q != cur.q) || (prev.q != cur.q && next.q == cur.q);
            var rBend = (prev.r == cur.r && next.r != cur.r) || (prev.r != cur.r && next.r == cur.r);
            if (qBend || rBend) {
                bends.add(cur);
            }
        });
    }

    /**
     * Returns the bends from a list of adjacent hexes (a chain).
     *
     * @param mapTiles the hex chain
     */
    void calcDirections(final List<MapTile> mapTiles) {

        List<Integer> directions = new ArrayList<>();
        int end = mapTiles.size() - 1;
        for (var i = 0; i < end; i++) {
            var cur = mapTiles.get(i).getHex();
            var next = mapTiles.get(i + 1).getHex();

            //directions
            int direction = cur.getDirectionTo(next);
            directions.add(direction);

            if (i == end-1) {
                directions.add(direction); //for the last tile to the target, which is always orthogonal
            }
        }

        for (int i = 0, hexesSize = mapTiles.size(); i < hexesSize; i++) {
            MapTile hex = mapTiles.get(i);
            hex.setPathDirection(directions.get(i));
        }
    }

    public List<Hex> getBends() {
        return Collections.unmodifiableList(bends);
    }
}
