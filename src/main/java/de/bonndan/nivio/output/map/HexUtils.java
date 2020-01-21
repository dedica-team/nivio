package de.bonndan.nivio.output.map;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class HexUtils {

    public static final List<Hex> DIRECTIONS = new ArrayList<>();

    static {
        DIRECTIONS.add(new Hex(1, 0, -1));
        DIRECTIONS.add(new Hex(1, -1, 0));
        DIRECTIONS.add(new Hex(0, -1, 1));
        DIRECTIONS.add(new Hex(-1, 0, 1));
        DIRECTIONS.add(new Hex(-1, 1, 0));
        DIRECTIONS.add(new Hex(0, 1, -1));
    }

    static Point2D.Double hexToPixel(Hex hex, Layout layout) {
        var s = layout.spacing;
        var M = layout.orientation;
        double x = (M.f0 * hex.q + M.f1 * hex.r) * layout.size.x;
        double y = (M.f2 * hex.q + M.f3 * hex.r) * layout.size.y;
        // Apply spacing
        x = x * s;
        y = y * s;
        return new Point2D.Double(x + layout.origin.x, y + layout.origin.y);
    }

    static double distance(Hex a, Hex b) {
        return HexUtils.lengths(HexUtils.subtract(a, b));
    }

    private static Hex add(Hex a, Hex b) {
        return new Hex(a.q + b.q, a.r + b.r, a.s + b.s);
    }

    private static Hex subtract(Hex a, Hex b) {
        return new Hex(a.q - b.q, a.r - b.r, a.s - b.s);
    }

    private static Hex neighbour(Hex hex, int direction) {
        return add(hex, HexUtils.direction(direction));
    }

    private static Integer lengths(Hex hex) {
        return Math.toIntExact((Math.abs(hex.q) + Math.abs(hex.r) + Math.abs(hex.s)) / 2);
    }

    private static Hex direction(int _direction) {
        return DIRECTIONS.get((6 + _direction % 6) % 6);
    }

    static List<Hex> neighbours(Hex hex) {
        List<Hex> n = new ArrayList<>();
        for (var i = 0; i < HexUtils.DIRECTIONS.size(); i += 1) {
            n.add(neighbour(hex, i));
        }

        return n;
    }
}
