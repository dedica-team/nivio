import {HexUtils} from 'react-hexgrid';
import TilePath from "./TilePath";

class PathFinder {

    constructor(occupied) {
        this.occupied = occupied;
    }

    findPaths(paths, target) {

        paths.forEach(path => {
            let source = path.tiles[path.tiles.length - 1];
            let distance = HexUtils.distance(source, target);

            if (distance === 0) {
                path.closed = true;
            }

            let possibleSteps = HexUtils.neighbours(source)
                .filter(neigh => this.isSame(neigh, target) || !this.isOccupied(neigh))
                .filter(neigh => {
                    //return neighbours which are closer to the target
                    let ndist = HexUtils.distance(neigh, target);
                    return ndist < distance;
                });
            /*console.log(possibleSteps.length + " poss. steps (" +
                possibleSteps.map(hex => hex.q + "," + hex.r).join("; ")
                + ") in distance " + distance + " from " + source.q + "," + source.r
                + " to " + target.q + "," + target.r);
             */

            if (possibleSteps.length === 0) { //TODO wrong, wont go back
                path.closed = true;
            } else {

                //prolong path and create clones if there are more possibilties
                let template = new TilePath();
                template.tiles = path.tiles;
                path.tiles.push(possibleSteps.shift());

                for (var i = 0; i < possibleSteps.length; i++) {
                    let clone = {...template};
                    //console.log("cloned path to add " + possibleSteps[i].q + "," + possibleSteps[i].r);
                    //console.log("new clone:");
                    //console.log(clone);
                    paths.push(clone);
                }
            }

        });

        //continue search if unclosed paths remain
        if (paths.find(path => !path.closed) !== undefined) {
            this.findPaths(paths, target)
        }

    }

    isOccupied(tile) {
        return this.occupied.find(o => this.isSame(o, tile)) !== undefined;
    }

    isSame(t1, t2) {
        return t1.r === t2.r && t1.q === t2.q;
    }
}

export default PathFinder;