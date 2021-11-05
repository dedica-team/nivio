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
    public static final int startAngle = 0;

    /**
     * clockwise neighbours, starting with "southeast"
     */
    public static final List<Hex> DIRECTIONS = List.of(
            new Hex(1, 0, -1),
            new Hex(0, 1, -1), //south
            new Hex(-1, 1, 0),
            new Hex(-1, 0, 1),
            new Hex(0, -1, 1), //north
            new Hex(1, -1, 0)
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

    /**
     * Using this constructor is discouraged, since only q and r are needed.
     */
    public Hex(int q, int r, int s) {
        if (q + r + s != 0) {
            throw new IllegalArgumentException("q + r + s must be 0");
        }
        this.q = q;
        this.r = r;
        this.s = s;
    }

    /**
     * https://www.redblobgames.com/grids/hexagons/implementation.html
     *
     * TLDR: q and r and like x and y and sufficient to describe a hex position. s is a third axis orthogonal to q and r.
     * @param q coordinate
     * @param r coordinate
     */
    public Hex(int q, int r) {
        this(q, r, (r + q) * -1);
    }

    /**
     * Creates a hexmap coord representation of the given coordinates.
     *
     * @param x map item x coord
     * @param y map item y coord
     * @return hex with q,r coordinates derived from x,y coords (rounded)
     */
    public static Hex of(long x, long y) {
        return of(x, y, 1f);
    }

    public static Hex of(long x, long y, float scaling) {
        var q = (2. / 3 * x) / (DEFAULT_ICON_SIZE * scaling);
        var r = (-1. / 3 * x + Math.sqrt(3) / 3 * y) / (DEFAULT_ICON_SIZE * scaling);

        double s = -q - r;
        if (Math.round(q + r + s) != 0) {
            throw new IllegalArgumentException("q + r + s must be 0");
        }

        int qi = (int) Math.round(q);
        int ri = (int) Math.round(r);
        int si = (int) Math.round(s);

        var q_diff = Math.abs(qi - q);
        var r_diff = Math.abs(ri - r);
        var s_diff = Math.abs(si - s);

        if (q_diff > r_diff && q_diff > s_diff) {
            qi = -ri - si;
        } else if (r_diff > s_diff) {
            ri = -qi - si;
        } else {
            si = -qi - ri;
        }
        return new Hex(qi, ri, si);
    }

    /**
     * Returns the distance to the target hex in number of tiles.
     *
     * https://www.redblobgames.com/grids/hexagons/#distances
     *
     * @param target target hex
     * @return number of tiles
     */
    public int distance(Hex target) {
        Hex hex = this.subtract(target);
        double l = (Math.abs(hex.q) + Math.abs(hex.r) + Math.abs(hex.s)) / 2.0;
        return (int) Math.round(l);
    }

    @NonNull
    public List<Hex> neighbours() {
        List<Hex> n = new ArrayList<>();
        for (var i = 0; i < DIRECTIONS.size(); i += 1) {
            n.add(neighbour(this, i));
        }
        return n;
    }

    /**
     * Return the leftmost (q coord) of the highest (r coord) hexes.
     *
     * @param area all hexes in the area (unsorted)
     * @return
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
        List<Hex> neighbours = neighbours();
        for (int i = 0, neighboursSize = neighbours.size(); i < neighboursSize; i++) {
            Hex hex1 = neighbours.get(i);
            if (hex1.equals(hex)) return i;
        }

        throw new IllegalArgumentException("Not an adjacent hex given.");
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

    private static Hex add(Hex a, Hex b) {
        return new Hex(a.q + b.q, a.r + b.r, a.s + b.s);
    }

    private Hex subtract(Hex b) {
        return new Hex(this.q - b.q, this.r - b.r, this.s - b.s);
    }

    private Hex neighbour(Hex hex, int direction) {
        return add(hex, this.direction(direction));
    }

    private Hex direction(int _direction) {
        return DIRECTIONS.get((6 + _direction % 6) % 6);
    }

    /**
     * see https://www.redblobgames.com/grids/hexagons/implementation.html#hex-geometry
     *
     * @param corner number
     * @param size hex size
     */
    public static Point2D.Double getCornerCoordinates(float corner, int size) {
        Point2D.Double point = new Point2D.Double(size, size);
        double angle = 2.0 * Math.PI * (startAngle + corner) / 6;
        return new Point2D.Double(point.x * Math.cos(angle), point.y * Math.sin(angle));
    }

    /**
     * Returns the outline (corners) of a flat top hex as points.
     *
     * @param size
     * @return
     */
    public ArrayList<Point2D.Double> asPoints(int size) {

        ArrayList<Point2D.Double> corners = new ArrayList<>();
        Point2D.Double center = toPixel();
        for (int i = 0; i < 6; i++) {
            Point2D.Double offset = getCornerCoordinates(i, size);
            corners.add(
                    new Point2D.Double(Math.round((center.x + offset.x)*10)/10f, Math.round((center.y + offset.y)*10)/10f)
            );
        }
        return corners;
    }

    @Override
    public String toString() {
        return "Hex{" +
                "q=" + q +
                ", r=" + r +
                ", s=" + s +
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
