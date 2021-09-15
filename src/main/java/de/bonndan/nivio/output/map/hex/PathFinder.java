package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.output.map.svg.HexPath;
import org.apache.commons.collections4.BidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A* pathfinder based on https://github.com/helfsoft/astar (no license given).
 */
class PathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinder.class);
    public static final int DEPTH_MAX = 4000;
    private final BidiMap<Hex, Object> hexesToItems;

    public boolean debug = false;

    private final ArrayList<PathTile> closed;
    private final ArrayList<PathTile> open;

    public PathFinder(BidiMap<Hex, Object> hexesToItems) {
        this.hexesToItems = hexesToItems;
        this.closed = new ArrayList<>();
        this.open = new ArrayList<>();
    }


    /**
     * Returns the best fitting tile in openlist to add to path based on F cost
     * of tile.
     *
     * @return The best tile for path in openlist
     */
    private PathTile getBest() {

        PathTile tmp = open.get(open.size() - 1);
        for (int i = open.size() - 1; i >= 0; i--) {

            // pick the lowest sum of move and heuristic
            boolean lowestCost = open.get(i).sumCosts < tmp.sumCosts;
            boolean equalCostLowerHeuristic = open.get(i).sumCosts == tmp.sumCosts && open.get(i).heuristicCosts < tmp.heuristicCosts;
            if (lowestCost || equalCostLowerHeuristic) {
                tmp = open.get(i);
            }
        }

        open.remove(tmp);

        if (debug) LOGGER.debug("returning best tile {} with sumCosts {} and heuristic {}", tmp, tmp.sumCosts, tmp.heuristicCosts);
        return tmp;
    }

    /**
     * Calculates the path between two tiles.
     *
     * @param startHex The start hex of the path
     * @param destHex  The destination hex of the path
     * @return A list containing all tiles along the path between start and dest or nothing if no path was found
     */
    public Optional<HexPath> getPath(Hex startHex, Hex destHex) {
        closed.clear();
        open.clear();
        var start = new PathTile(startHex);
        var dst = new PathTile(destHex);


        PathTile currentStep = start;
        open.add(0, currentStep);

        int G;

        int depth = 0;

        while (true) {

            /*
             * Limit the amount of loops for better performance
             */
            if (depth >= DEPTH_MAX) {
                LOGGER.error("Max depth exceeded searching path from {} to {}", startHex, destHex);
                return Optional.empty();
            }

            /*
             * If the tile which is currently checked (currentStep) is the
             * destination tile search can be stopped (break). The same goes for
             * an empty list of potential tiles suited for path (openlist).
             */
            if (currentStep.equals(dst)) {
                dst.parent = currentStep.parent;
                break;
            } else if (open.isEmpty()) {
                break;
            }

            /*
             * Get tile with lowest F cost from openlist.
             */
            currentStep = getBest();

            /*
             * Add to closed list (tile already part of path).
             */
            closed.add(currentStep);

            /*
             * Check all neighbors of the currentstep.
             */
            var neighbours = getNeighbourTiles(currentStep);
            for (int i = 0; i < neighbours.size(); i++) {

                PathTile neighbour = neighbours.get(i);

                if (neighbour.equals(dst)) {
                    neighbour.setParent(currentStep);
                    currentStep = neighbour;
                    if (debug) LOGGER.debug("reached  {}", currentStep.hex);
                    break;
                }

                if (closed.contains(neighbour)) {
                    continue;
                }

                /*
                 * Get the moving costs from the currentstep to the
                 * neighbor.
                 */
                G = neighbour.calcMoveCostsFrom(currentStep);

                if (!open.contains(neighbour)) {
                    open.add(neighbour);
                } else if (G >= neighbour.moveCosts) {
                    continue;
                }

                neighbour.parent = currentStep;
                neighbour.moveCosts = G;
                neighbour.calcHeuristic(dst);
                neighbour.sumHeuristicAndMoveCosts();
            }

            depth += 1;
        }

        /*
         * Build the path reversly iterating over the tiles by accessing their
         * parent tile.
         */
        ArrayList<PathTile> path = new ArrayList<>();
        path.add(dst);
        PathTile tileBetween = dst;
        while (!start.equals(tileBetween)) {

            if (tileBetween.getParent() == null) {
                break;
            }

            tileBetween = tileBetween.getParent();
            if (path.contains(tileBetween)) { //already contains the parent
                throw new IllegalStateException("Path already contains the parent.");
            }
            if (!dst.equals(tileBetween) && !start.equals(tileBetween) && tileBetween.hex.item != null) {
                throw new IllegalStateException(String.format("Path from %s to %s runs through item %s!", start, dst, tileBetween));
            }

            path.add(tileBetween);
        }

        /*
         * Reverse to get the path from start to dst.
         */
        Collections.reverse(path);

        /*
         * If no path is found return null.
         */
        if (path.isEmpty()) {
            return Optional.empty();
        }

        List<Hex> hexes = new ArrayList<>();
        for (PathTile tile : path) {
            hexes.add(tile.hex);
        }
        return Optional.of(new HexPath(hexes));
    }

    private List<PathTile> getNeighbourTiles(PathTile current) {

        List<PathTile> neighbours = new ArrayList<>();
        current.hex.neighbours().forEach(hex -> {
            Object val = hexesToItems.get(hex);
            if (val != null) {
                hex = hexesToItems.inverseBidiMap().get(val);
            }
            neighbours.add(new PathTile(hex));
        });
        return neighbours;
    }

}
