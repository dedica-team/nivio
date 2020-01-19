package de.bonndan.nivio.output.map;

import java.util.List;
import java.util.stream.Collectors;

class PathFinder {

    private final List<Hex> occupied;

    PathFinder(List<Hex> occupied) {
        this.occupied = occupied;
    }

    TilePath findPaths(List<TilePath> paths, Hex target) {

        paths.forEach(path -> {
            var source = path.tiles.get(path.tiles.size() - 1);
            var distance = HexUtils.distance(source, target);

            if (distance == 0) {
                path.close();
            }

            List<Hex> possibleSteps = HexUtils.neighbours(source).stream()
                .filter(neigh -> this.isSame(neigh, target) || !this.isOccupied(neigh))
                .filter(neigh -> {
                    //return neighbours which are closer to the target
                    var ndist = HexUtils.distance(neigh, target);
                    return ndist < distance;
                }).collect(Collectors.toList());

            /*console.log(possibleSteps.length + " poss. steps (" +
                possibleSteps.map(hex => hex.q + "," + hex.r).join("; ")
                + ") in distance " + distance + " from " + source.q + "," + source.r
                + " to " + target.q + "," + target.r);
             */

            if (possibleSteps.size() == 0) { //TODO wrong, wont go back
                path.close();
            } else {

                //prolong path and create clones if there are more possibilties
                var template = new TilePath(null);
                template.tiles = path.tiles;
                path.tiles.add(possibleSteps.remove(0));

                for (var i = 0; i < possibleSteps.size(); i++) {
                    var clone = new TilePath(null);
                    path.tiles.forEach(tile -> clone.tiles.add(tile));
                    clone.tiles.remove(clone.tiles.size()-1);
                    //console.log("cloned path to add " + possibleSteps[i].q + "," + possibleSteps[i].r);
                    //console.log("new clone:");
                    //console.log(clone);
                    clone.tiles.add(possibleSteps.get(i));
                    paths.add(clone);
                }
            }

        });

        //continue search if unclosed paths remain
        if (paths.stream().anyMatch(path -> !path.isClosed())) {
            return this.findPaths(paths, target);
        } else {
            return this.sortAndFilterPaths(paths);
        }

        //TODO pick one path, mark tiles as occupied to avoid path crossings
    }

    boolean isOccupied(Hex tile) {
        return this.occupied.stream().anyMatch(o -> this.isSame(o, tile));
    }

    boolean isSame(Hex t1, Hex t2) {
        return t1.r == t2.r && t1.q == t2.q;
    }


    TilePath sortAndFilterPaths(List<TilePath> paths) {
        paths.sort((first, second) -> {
            if (first.getSpeed() == second.getSpeed())
                return 0;

            return first.getSpeed() > second.getSpeed() ? -1 : 1;
        });
        return paths.get(0);
    }
}
