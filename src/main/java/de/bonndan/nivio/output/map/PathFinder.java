package de.bonndan.nivio.output.map;

import de.bonndan.nivio.output.map.hex.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

class PathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinder.class);
    private final List<Hex> occupied;

    PathFinder(List<Hex> occupied) {
        this.occupied = occupied;
    }

    TilePath findPaths(List<TilePath> paths, final Hex target) {

        paths.forEach(path -> {
            Hex pathEnd = path.tiles.get(path.tiles.size() - 1);
            var distance = pathEnd.distance(target);

            if (distance == 0.0) {
                LOGGER.debug("distance 0 to target {} reached at {}", target, pathEnd);
                path.close();
                return;
            }

            List<Hex> free = pathEnd.neighbours().stream()
                    .filter(neigh -> this.isSame(neigh, target) || !this.isOccupied(neigh))
                    .collect(Collectors.toList());
            LOGGER.debug("{} free tiles at {}", free.size(), pathEnd);

            //return free neighbours which are closer to the target
            List<Hex> possibleSteps = getPossibleSteps(free, pathEnd, target, distance);

            if (possibleSteps.size() == 0) { //TODO wrong, wont go back
                LOGGER.warn("no more possible steps, closing path {} at ", pathEnd);
                path.close();
            } else {

                //prolong path and create clones if there are more possibilties
                var template = new TilePath(null);
                template.tiles = path.tiles;
                path.tiles.add(possibleSteps.remove(0));

                for (var i = 0; i < possibleSteps.size(); i++) {
                    var clone = new TilePath(null);
                    path.tiles.forEach(tile -> clone.tiles.add(tile));
                    clone.tiles.remove(clone.tiles.size() - 1);
                    var nexTile = possibleSteps.get(i);
                    System.out.println("cloned path to add " + nexTile.q + "," + nexTile.r);
                    clone.tiles.add(nexTile);
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

    //return free neighbours which are closer to the target
    private List<Hex> getPossibleSteps(List<Hex> free, Hex pathEnd, Hex target, int distance) {
        if (free.isEmpty()) {
            return free;
        }

        free.sort((hex, t1) -> {
            return Integer.valueOf(hex.distance(target)).compareTo(t1.distance(target));
        });

        Hex first = free.get(0);
        int min = first.distance(target);
        List<Hex> nearest = free.stream().filter(hex -> hex.distance(target) <= min).collect(Collectors.toList());

        String tmp = nearest.stream().map(hex -> hex.q + "," + hex.r).collect(Collectors.joining(";"));
        LOGGER.debug("'{}' tiles at {} closer (distance < {}) to {}", tmp, pathEnd, distance, target);
        LOGGER.debug(
                nearest.size() + " poss. steps (" + tmp + ") in distance " + distance
                        + " from " + pathEnd.q + "," + pathEnd.r
                        + " to " + target.q + "," + target.r
        );

        return nearest;
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
