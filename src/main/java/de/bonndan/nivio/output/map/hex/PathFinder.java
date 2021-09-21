package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.output.map.svg.HexPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A* pathfinder based on https://github.com/helfsoft/astar (no license given).
 */
class PathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinder.class);
    public static final int MAX_ITERATIONS = 5000;

    @NonNull
    private final HexMap hexMap;
    private final ArrayList<PathTile> closed;
    private final ArrayList<PathTile> open;
    private final boolean debug;

    static PathFinder withEmptyMap() {
        return new PathFinder(new HexMap(false), false);
    }

    /**
     * @param hexMap the map containing all hexes
     * @param debug
     */
    PathFinder(@NonNull final HexMap hexMap, boolean debug) {
        this.hexMap = hexMap;
        this.debug = debug;
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

        PathTile lastTile = open.get(open.size() - 1);
        for (int i = open.size() - 2; i >= 0; i--) {

            // pick the lowest sum of move and heuristic
            PathTile pathTile = open.get(i);
            boolean lowestCost = pathTile.sumCosts < lastTile.sumCosts;
            boolean equalCostLowerHeuristic = pathTile.sumCosts == lastTile.sumCosts && pathTile.heuristicCosts < lastTile.heuristicCosts;
            if (lowestCost || equalCostLowerHeuristic) {
                lastTile = pathTile;
            }
        }

        open.remove(lastTile);

        if (debug)
            LOGGER.debug("returning best tile {} with sumCosts {} and heuristic {}", lastTile, lastTile.sumCosts, lastTile.heuristicCosts);
        return lastTile;
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
        var destination = new PathTile(destHex);


        PathTile currentStep = start;
        open.add(0, currentStep);

        int G;

        int iterations = 0;

        while (true) {

            /*
             * Limit the amount of loops for better performance
             */
            if (iterations >= MAX_ITERATIONS) {
                LOGGER.error("Max iterations exceeded searching path from {} to {}", startHex, destHex);
                return Optional.empty();
            }

            /*
             * If the tile which is currently checked (currentStep) is the
             * destination tile search can be stopped (break). The same goes for
             * an empty list of potential tiles suited for path (openlist).
             */
            if (currentStep.equals(destination)) {
                destination.parent = currentStep.parent;
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
             * Check all neighbors of the current step.
             */
            var neighbours = getNeighbourTiles(currentStep);
            for (PathTile neighbour : neighbours) {

                if (neighbour.equals(destination)) {
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
                    neighbour.parent = currentStep;
                    neighbour.moveCosts = G;
                    neighbour.calcHeuristicToDestinationAndSum(destination);
                    open.add(neighbour);
                }
            }

            iterations += 1;
        }

        /*
         * Build the path reversly iterating over the tiles by accessing their
         * parent tile.
         */
        ArrayList<PathTile> path = new ArrayList<>();
        path.add(destination);
        PathTile tileBetween = destination;
        while (!start.equals(tileBetween)) {

            tileBetween = tileBetween.getParent();
            if (tileBetween == null) {
                break;
            }

            if (path.contains(tileBetween)) { //already contains the parent
                throw new IllegalStateException("Path already contains the parent.");
            }

            //we accept that items can be crossed, although it is very unpleasant
            if (!destination.equals(tileBetween) && !start.equals(tileBetween) && tileBetween.hex.item != null) {
                LOGGER.error("Path from {} to {} runs through item {}!", start, destination, tileBetween);
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
        hexMap.getNeighbours(current.hex).forEach(hex -> neighbours.add(new PathTile(hex)));
        return neighbours;
    }

}
