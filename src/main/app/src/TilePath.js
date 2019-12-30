import {HexUtils} from "react-hexgrid";

class TilePath {

    constructor(tile) {
        this.tiles = [];
        this.bends = [];
        if (tile !== undefined)
            this.tiles.push(tile);
        this.closed = false;
    }

    close() {
        if (this.closed)
            return;

        this.closed = true;

        let i =0;
        for (i = 1; i < this.tiles.length - 1; i++) {
            let prev = this.tiles[i - 1];
            let cur = this.tiles[i];
            let next = this.tiles[i+1];
            let qBend = (prev.q === cur.q && next.q !== cur.q) || (prev.q !== cur.q && next.q === cur.q);
            let rBend = (prev.r === cur.r && next.r !== cur.r) || (prev.r !== cur.r && next.r === cur.r);
            if (qBend || rBend)
                this.bends.push(cur);
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
        points += this.tiles.map(hex => {
            let p = HexUtils.hexToPixel(hex, layout);
            return ` ${p.x},${p.y} `;
        }).join('L');

        return points;
    }

    getSpeed() {
        return this.tiles.length - this.bends.length;
    }
}

export default TilePath;