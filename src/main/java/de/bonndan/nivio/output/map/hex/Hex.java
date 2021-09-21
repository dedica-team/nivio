package de.bonndan.nivio.output.map.hex;

import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static de.bonndan.nivio.output.map.svg.SVGRenderer.DEFAULT_ICON_SIZE;

/**
 * This is all copied code from https://www.redblobgames.com/grids/hexagons/implementation.html#hex-geometry and several
 * stackoverflow questions.
 */
public class Hex {

    //was layout origin
    public static final Point2D.Double origin = new Point2D.Double(200, 200);

    /**
     * starting at east, right before the first neighbour (which is southeast (r+1) in clockwise direction)
     */
    public static final int START_ANGLE = 0;

    /**
     * clockwise neighbours, starting with "southeast"
     */
    public static final List<Hex> DIRECTIONS = List.of(
            new Hex(1, 0),
            new Hex(0, 1), //south
            new Hex(-1, 1),
            new Hex(-1, 0),
            new Hex(0, -1), //north
            new Hex(1, -1)
    );

    //double DEFAULT_ICON_SIZE
    public static final int HEX_SIZE = 2 * DEFAULT_ICON_SIZE;

    public static final int SOUTH_EAST = 0;
    public static final int SOUTH = 1;
    public static final int SOUTH_WEST = 2;
    public static final int NORTH_WEST = 3;
    public static final int NORTH = 4;
    public static final int NORTH_EAST = 5;

    /**
     * q coordinate
     *
     * For coords see https://www.redblobgames.com/grids/hexagons/#coordinates
     */
    public final int q;

    /**
     * r coordinate
     */
    public final int r;

    /**
     * s coordinate (could be omitted here, since q + r + s = 0 is mandatory)
     */
    public final int s;

    public String item;
    public String group;
    private Integer pathDirection;

    /**
     * https://www.redblobgames.com/grids/hexagons/implementation.html
     *
     * TLDR: q and r and like x and y and sufficient to describe a hex position. s is a third axis orthogonal to q and r.
     *
     * @param q coordinate
     * @param r coordinate
     */
    public Hex(int q, int r) {
        this.q = q;
        this.r = r;
        this.s = (r + q) * -1;
    }

    /**
     * Returns the distance to the target hex as number of tiles.
     *
     * https://www.redblobgames.com/grids/hexagons/#distances
     *
     * @param target target hex
     * @return number of tiles
     */
    public int distance(Hex target) {
        Hex hex = new Hex(this.q - target.q, this.r - target.r);
        double l = (Math.abs(hex.q) + Math.abs(hex.r) + Math.abs(hex.s)) / 2.0;
        return (int) Math.round(l);
    }

    /**
     * Returns hexes unrelated to a map
     *
     * @param hex center
     * @return neighbours, not for use with map operations!
     */
    @NonNull
    public static List<Hex> neighbours(Hex hex) {
        List<Hex> n = new ArrayList<>();
        for (var i = 0; i < DIRECTIONS.size(); i += 1) {
            Hex dir = DIRECTIONS.get((6 + i % 6) % 6);
            n.add(new Hex(hex.q + dir.q, hex.r + dir.r));
        }
        return n;
    }

    /**
     * Return the leftmost (q coord) of the highest (r coord) hexes.
     *
     * @param area all hexes in the area (unsorted)
     */
    public static Hex topLeft(Collection<Hex> area) {
        AtomicInteger q = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger r = new AtomicInteger(Integer.MAX_VALUE);
        AtomicReference<Hex> topLeft = new AtomicReference<>(null);
        area.forEach(hex -> {
            //higher
            if (hex.r < r.get()) {
                r.set(hex.r);
                topLeft.set(hex);
            }

            if (hex.r == r.get()) {
                if (topLeft.get() == null || hex.q < q.get()) {
                    q.set(hex.q);
                    topLeft.set(hex);
                }
            }
        });

        return topLeft.get();
    }

    public int getDirectionTo(@NonNull final Hex hex) {
        List<Hex> neighbours = Hex.neighbours(this);
        for (int i = 0, neighboursSize = neighbours.size(); i < neighboursSize; i++) {
            Hex hex1 = neighbours.get(i);
            if (hex1.q == hex.q && hex1.r == hex.r) return i;
        }

        throw new IllegalArgumentException(String.format("Hex %s: not an adjacent hex %s given to determine direction.", this, hex));
    }

    public void setPathDirection(int pathDirection) {
        this.pathDirection = pathDirection;
    }

    public Integer getPathDirection() {
        return pathDirection;
    }


    /**
     * flat orientation (flat top)
     */
    static class FlatOrientation {
        public static final double f0 = 3.0 / 2.0;
        public static final double f1 = 0.0;
        public static final double f2 = Math.sqrt(3.0) / 2.0;
        public static final double f3 = Math.sqrt(3.0);
    }

    public Point2D.Double toPixel() {
        double x = (FlatOrientation.f0 * this.q + FlatOrientation.f1 * this.r) * HEX_SIZE;
        double y = (FlatOrientation.f2 * this.q + FlatOrientation.f3 * this.r) * HEX_SIZE;
        return new Point2D.Double(x + origin.x, y + origin.y);
    }

    /**
     * see https://www.redblobgames.com/grids/hexagons/implementation.html#hex-geometry
     *
     * @param corner number
     * @param size   hex size
     */
    public static Point2D.Double getCornerCoordinates(float corner, int size) {
        Point2D.Double point = new Point2D.Double(size, size);
        double angle = 2.0 * Math.PI * (START_ANGLE + corner) / 6;
        return new Point2D.Double(point.x * Math.cos(angle), point.y * Math.sin(angle));
    }

    /**
     * Returns the outline (corners) of a flat top hex as points.
     *
     * @param size
     * @return
     */
    public List<Point2D.Double> asPoints(int size) {

        ArrayList<Point2D.Double> corners = new ArrayList<>();
        Point2D.Double center = toPixel();
        for (int i = 0; i < 6; i++) {
            Point2D.Double offset = getCornerCoordinates(i, size);
            corners.add(
                    new Point2D.Double(Math.round((center.x + offset.x) * 10) / 10f, Math.round((center.y + offset.y) * 10) / 10f)
            );
        }
        return corners;
    }

    @Override
    public String toString() {
        return "Hex{" +
                "q=" + q +
                ", r=" + r +
                ", id='" + item + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hex hex = (Hex) o;

        if (q != hex.q) return false;
        if (r != hex.r) return false;
        return s == hex.s;
    }

    @Override
    public int hashCode() {
        int result = q;
        result = 31 * result + r;
        result = 31 * result + s;
        return result;
    }

}
