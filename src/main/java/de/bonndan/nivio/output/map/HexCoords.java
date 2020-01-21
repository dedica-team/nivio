package de.bonndan.nivio.output.map;

/**
 * Calculated hexagon map coordinates from x-y coordinates.
 *
 * https://stackoverflow.com/questions/20734438/algorithm-to-generate-a-hexagonal-grid-with-coordinate-system/20751975#20751975
 * https://www.redblobgames.com/grids/hexagons/#rounding
 */
class HexCoords {

    private double q;
    private double r;
    private double s;

    HexCoords(long x, long y, int size) {
        var q = (2. / 3 * x) / size;
        var r = (-1. / 3 * x + Math.sqrt(3) / 3 * y) / size;

        double s1 = -q - r;
        if (Math.round(q + r + s1) != 0) {
            throw new RuntimeException("q + r + s must be 0");
        }

        this.q = q;
        this.r = r;
        this.s = s1;
    }

    Hex toHex() {
        var qi = Math.round(this.q);
        var ri = Math.round(this.r);
        var si = Math.round(this.s);

        var q_diff = Math.abs(qi - this.q);
        var r_diff = Math.abs(ri - this.r);
        var s_diff = Math.abs(si - this.s);

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
