package de.bonndan.nivio.output.map.hex;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This is all copied code from https://www.redblobgames.com/grids/hexagons/implementation.html#hex-geometry and several
 * stackoverflow questions.
 */
public class Hex {

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
    public static final int HEX_SIZE = 100;

    public final int q;
    public final int r;
    public final int s;
    public String id;

    public Hex(int q, int r, int s) {
        if (q + r + s != 0) {
            throw new RuntimeException("q + r + s must be 0");
        }
        this.q = q;
        this.r = r;
        this.s = s;
    }

    public int distance(Hex target) {
        return lengths(this.subtract(target));
    }

    public List<Hex> neighbours() {
        List<Hex> n = new ArrayList<>();
        for (var i = 0; i < DIRECTIONS.size(); i += 1) {
            n.add(neighbour(this, i));
        }
        return n;
    }

    public Point2D.Double toPixel() {
        double x = (FlatOrientation.f0 * this.q + FlatOrientation.f1 * this.r) * Layout.SIZE;
        double y = (FlatOrientation.f2 * this.q + FlatOrientation.f3 * this.r) * Layout.SIZE;
        return new Point2D.Double(x + Layout.origin.x, y + Layout.origin.y);
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

    private static Integer lengths(Hex hex) {
        double l = (Math.abs(hex.q) + Math.abs(hex.r) + Math.abs(hex.s)) / 2.0;
        return (int) Math.round(l);
    }

    private Hex direction(int _direction) {
        return DIRECTIONS.get((6 + _direction % 6) % 6);
    }

    /**
     * see https://www.redblobgames.com/grids/hexagons/implementation.html#hex-geometry
     */
    private Point2D.Double hex_corner_offset(int corner, int s) {
        Point2D.Double size = new Point2D.Double(s, s);
        double angle = 2.0 * Math.PI * (Hex.Layout.startAngle + corner) / 6;
        return new Point2D.Double(size.x * Math.cos(angle), size.y * Math.sin(angle));
    }

    public ArrayList<Point2D.Double> asPoints(int size) {

        ArrayList<Point2D.Double> corners = new ArrayList<>();
        Point2D.Double center = toPixel();
        for (int i = 0; i < 6; i++) {
            Point2D.Double offset = hex_corner_offset(i, size);
            corners.add(
                    new Point2D.Double(Math.round(center.x + offset.x), Math.round(center.y + offset.y))
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
                ", id='" + id + '\'' +
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

    /**
     * flat orientation (flat top)
     */
    static class FlatOrientation {
        public static final double f0 = 3.0 / 2.0;
        public static final double f1 = 0.0;
        public static final double f2 = Math.sqrt(3.0) / 2.0;
        public static final double f3 = Math.sqrt(3.0);
    }

    public static class Layout {

        static final int SIZE = 100;
        public static final Point2D.Double origin = new Point2D.Double(200, 200);

        /**
         * starting at east, right before the first neighbour (which is southeast (r+1) in clockwise direction)
         */
        public static final int startAngle = 0;
    }
}
