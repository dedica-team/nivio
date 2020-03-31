/**
 * Calculated hexagon map coordinates from x-y coordinates.
 *
 * https://stackoverflow.com/questions/20734438/algorithm-to-generate-a-hexagonal-grid-with-coordinate-system/20751975#20751975
 * https://www.redblobgames.com/grids/hexagons/#rounding
 */
class HexCoords {
  constructor(x, y, size) {
    let q = ((2 / 3) * x) / size;
    let r = ((-1 / 3) * x + (Math.sqrt(3) / 3) * y) / size;

    this.fractionalHex(q, r, -q - r);
  }

  fractionalHex(q, r, s) {
    if (Math.round(q + r + s) !== 0) {
      throw 'q + r + s must be 0';
    }

    this.q = q;
    this.r = r;
    this.s = s;
  }

  toHex() {
    let qi = Math.round(this.q);
    let ri = Math.round(this.r);
    let si = Math.round(this.s);

    let q_diff = Math.abs(qi - this.q);
    let r_diff = Math.abs(ri - this.r);
    let s_diff = Math.abs(si - this.s);

    if (q_diff > r_diff && q_diff > s_diff) {
      qi = -ri - si;
    } else if (r_diff > s_diff) {
      ri = -qi - si;
    } else {
      si = -qi - ri;
    }
    return { q: qi, r: ri, s: si };
  }
}

export default HexCoords;
