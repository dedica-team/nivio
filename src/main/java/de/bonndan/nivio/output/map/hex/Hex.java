package de.bonndan.nivio.output.map.hex;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Hex {

    public static final List<Hex> DIRECTIONS = new ArrayList<>();

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

    @Override
    public String toString() {
        return "Hex{" +
                "q=" + q +
                ", r=" + r +
                ", s=" + s +
                ", id='" + id + '\'' +
                '}';
    }
}
