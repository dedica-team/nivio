package de.bonndan.nivio.output.map.hex;

public class HexFactory {

    private final int scaleFactor = 50;

    /**
     * Creates a hexmap coord representation of the given coordinates.
     *
     * @param x map item x coord
     * @param y map item y coord
     * @return hex with q,r coordinates derived from x,y coords (rounded)
     */
    public Hex of(long x, long y) {
        var q = (2. / 3 * x) / scaleFactor;
        var r = (-1. / 3 * x + Math.sqrt(3) / 3 * y) / scaleFactor;

        double s = -q - r;
        if (Math.round(q + r + s) != 0) {
            throw new RuntimeException("q + r + s must be 0");
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
}
