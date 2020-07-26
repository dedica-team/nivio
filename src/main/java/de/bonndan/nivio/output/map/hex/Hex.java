package de.bonndan.nivio.output.map.hex;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Hex {

    public static final List<Hex> DIRECTIONS = new ArrayList<>();

    //double DEFAULT_ICON_SIZE
    public static final int HEX_SIZE = 100;

    static {
        DIRECTIONS.add(new Hex(1, 0, -1));
        DIRECTIONS.add(new Hex(1, -1, 0));
        DIRECTIONS.add(new Hex(0, -1, 1));
        DIRECTIONS.add(new Hex(-1, 0, 1));
        DIRECTIONS.add(new Hex(-1, 1, 0));
        DIRECTIONS.add(new Hex(0, 1, -1));
    }

    public final int q;
    public final int r;
    public final int s;
    public String id;

    public Hex(int q, int r, int s) {
        if (Math.round(q + r + s) != 0) {
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
        var M = Orientation.LAYOUT_FLAT;
        double x = (M.f0 * this.q + M.f1 * this.r) * Layout.SIZE;
        double y = (M.f2 * this.q + M.f3 * this.r) * Layout.SIZE;
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
            corners.add(new Point2D.Double(center.x + offset.x, center.y + offset.y));
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

    public static class Orientation {

        static Orientation LAYOUT_FLAT = new Orientation(3.0 / 2.0, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0), 2.0 / 3.0, 0.0, -1.0 / 3.0, Math.sqrt(3.0) / 3.0, 0.0);

        private final double startAngle;
        public double f0;
        public double f1;
        public double f2;
        public double f3;

        private final double b0;
        private final double b1;
        private final double b2;
        private final double b3;


        Orientation(double f0, double f1, double f2, double f3, double b0, double b1, double b2, double b3, double startAngle) {

            this.f0 = f0;
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
            this.b0 = b0;
            this.b1 = b1;
            this.b2 = b2;
            this.b3 = b3;
            this.startAngle = startAngle;
        }

    }

    public static class Layout {

        static final int SIZE = 100;
        public static final Point2D.Double origin = new Point2D.Double(200, 200);
        public static final int startAngle = 0;
    }
}
