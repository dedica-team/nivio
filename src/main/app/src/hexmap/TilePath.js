import { HexUtils } from 'react-hexgrid';

class TilePath {
  constructor(tile) {
    this.tiles = [];
    this.bends = [];
    if (tile !== undefined) this.tiles.push(tile);
    this.closed = false;
  }

  close() {
    if (this.closed) return;

    this.closed = true;

    let i = 0;
    for (i = 1; i < this.tiles.length - 1; i++) {
      let prev = this.tiles[i - 1];
      let cur = this.tiles[i];
      let next = this.tiles[i + 1];
      let qBend = (prev.q === cur.q && next.q !== cur.q) || (prev.q !== cur.q && next.q === cur.q);
      let rBend = (prev.r === cur.r && next.r !== cur.r) || (prev.r !== cur.r && next.r === cur.r);
      if (qBend || rBend) this.bends.push(cur);
    }
  }

  reducePoints() {
    //reduce tiles (not first or last)
    /*
        let reduced = [];
        console.log(drop);
        for (i = 0; i < this.tiles.length; i++) {
            if (drop.indexOf(i) !== -1)
                continue;
            reduced.push(this.tiles[i]);
        }
        this.tiles = reduced;
         */
  }

  getPoints(layout) {
    let points = 'M';
    for (var i = 0; i < this.tiles.length; i++) {
      let hex = this.tiles[i];
      if (this._isBend(hex)) {
        //cubic curve
        let prev = HexUtils.hexToPixel(this.tiles[i - 1], layout);
        let point = HexUtils.hexToPixel(this.tiles[i], layout);
        let next = HexUtils.hexToPixel(this.tiles[i + 1], layout);

        let newBefore = {};
        newBefore.x = prev.x + (point.x - prev.x) / 2;
        newBefore.y = prev.y + (point.y - prev.y) / 2;
        points += ` ${newBefore.x},${newBefore.y} `;
        //points = points.substr(0, points.length-1);
        points += `Q ${point.x},${point.y} `;

        let newAfter = {};
        newAfter.x = next.x + (point.x - next.x) / 2;
        newAfter.y = next.y + (point.y - next.y) / 2;
        points += ` ${newAfter.x},${newAfter.y} L`;
      } else {
        let p = HexUtils.hexToPixel(hex, layout);
        points += ` ${p.x},${p.y} L`;
      }
    }
    return points.substr(0, points.length - 1);
  }

  _isBend(hex) {
    if (this.bends.length === 0) return false;

    for (var i = 0; i < this.bends.length; i++) {
      if (this.bends[i].q === hex.q && this.bends[i].r === hex.r) return true;
    }

    return false;
  }

  getSpeed() {
    return this.tiles.length - this.bends.length;
  }
}

export default TilePath;
