import {HexUtils} from "react-hexgrid";

class TilePath {

    constructor(tile) {
        this.tiles = [];
        if (tile !== undefined)
            this.tiles.push(tile);
        this.closed = false;
    }


    getPoints(layout) {
        let points = 'M';
        points += this.tiles.map(hex => {
            let p = HexUtils.hexToPixel(hex, layout);
            return ` ${p.x},${p.y} `;
        }).join('L');

        return points;
    }
}

export default TilePath;