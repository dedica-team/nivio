package de.bonndan.nivio.output.map;

import de.bonndan.nivio.output.map.hex.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class PathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinder.class);
    private final Set<Hex> occupied;
    private final AtomicInteger iterations = new AtomicInteger(0);
    private long start;
    private long end;
    public boolean debug = false;

    PathFinder(List<Hex> occupied) {
        this(new HashSet<>(occupied));
    }

    public PathFinder(Set<Hex> occupied) {
        this.occupied = occupied;
    }

    TilePath findBestPath(Hex start, Hex target) {
        var paths = new CopyOnWriteArrayList<TilePath>();
        paths.add(new TilePath(start));
        return findPaths(paths, target);
    }

    TilePath findPaths(List<TilePath> paths, final Hex target) {

        if (iterations.get() == 0) {
            this.start = Instant.now().toEpochMilli();
        }
        iterations.incrementAndGet();

        List<TilePath> remainingPaths = new ArrayList<>();
        paths.forEach(path -> {
            remainingPaths.add(path);
            Hex pathEnd = path.tiles.get(path.tiles.size() - 1);
            int distance = pathEnd.distance(target);

            if (distance == 0) {
                if (debug) LOGGER.debug("distance 0 to target {} reached at {}", target, pathEnd);
                path.close();
                return;
            }

            List<Hex> free = getFreeNeighbours(target, pathEnd);
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
                    clone.tiles.addAll(path.tiles);
                    clone.tiles.remove(clone.tiles.size() - 1);
                    var nexTile = possibleSteps.get(i);
                    //System.out.println("cloned path to add " + nexTile.q + "," + nexTile.r);
                    clone.tiles.add(nexTile);
                    remainingPaths.add(clone);
                }
            }
        });

        //continue search if unclosed paths remain
        if (remainingPaths.stream().anyMatch(path -> !path.isClosed())) {
            return this.findPaths(remainingPaths, target);
        } else {
            TilePath tilePath = this.sortAndFilterPaths(remainingPaths);
            this.end = Instant.now().toEpochMilli();
            return tilePath;
        }

        //TODO pick one path, mark tiles as occupied to avoid path crossings
    }

    private List<Hex> getFreeNeighbours(Hex target, Hex pathEnd) {
        List<Hex> neighbours = pathEnd.neighbours();
        List<Hex> free = new ArrayList<>();
        for (Hex neigh : neighbours) {
            if (neigh.equals(target) || !this.isOccupied(neigh)) {
                free.add(neigh);
            }
        }

        if (debug) {
            LOGGER.debug("{} free tiles at {}", free.size(), pathEnd);
        }
        return free;
    }

    //return free neighbours which are closer to the target
    private List<Hex> getPossibleSteps(List<Hex> free, Hex pathEnd, Hex target, int distance) {
        if (free.isEmpty()) {
            return free;
        }

        List<Hex> nearest = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        for (Hex hex1 : free) {
            int dist = hex1.distance(target);
            if (dist < min) {
                min = dist;
                nearest.clear();
                nearest.add(hex1);
                continue;
            }

            if (dist == min) {
                nearest.add(hex1);
            }
        }

        if (debug) {
            String tmp = nearest.stream().map(hex -> hex.q + "," + hex.r).collect(Collectors.joining(";"));
            LOGGER.debug("'{}' tiles at {} closer (distance < {}) to {}", tmp, pathEnd, distance, target);
            LOGGER.debug(
                    nearest.size() + " poss. steps (" + tmp + ") in distance " + distance
                            + " from " + pathEnd.q + "," + pathEnd.r
                            + " to " + target.q + "," + target.r
            );
        }

        return nearest;
    }

    private boolean isOccupied(Hex tile) {
        return occupied.contains(tile);
    }

    private TilePath sortAndFilterPaths(List<TilePath> paths) {
        paths.sort((first, second) -> {
            if (first.getSpeed() == second.getSpeed()) {
                return 0;
            }

            return first.getSpeed() > second.getSpeed() ? -1 : 1;
        });
        return paths.get(0);
    }

    long getTimeElapsed() {
        return end - start;
    }

    int getIterations() {
        return iterations.get();
    }
}
