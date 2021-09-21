package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.output.map.svg.HexPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

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

    /**
     * Costs for moving through an item
     */
    public static final int ITEM_PENALTY = 1000;

    /**
     * costs for moving on group area
     */
    public static final int GROUP_PENALTY = 3;

    /**
     * costs for moving on tile has is part of a path area
     */
    private static final int PATH_PENALTY = 2;

    /**
     * Regular movement costs.
     */
    public static final int BASE_COSTS = 1;

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
     * @param debug  flag to print debug info
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
    public Optional<HexPath> getPath(@NonNull final Hex startHex, @NonNull final Hex destHex) {
        closed.clear();
        open.clear();
        var start = new PathTile(startHex);
        var destination = new PathTile(destHex);

        PathTile currentStep = start;
        open.add(0, currentStep);

        int moveCosts;

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
             * an empty list of potential tiles suited for path (open list).
             */
            if (currentStep.equals(destination)) {
                destination.parent = currentStep.parent;
                break;
            } else if (open.isEmpty()) {
                break;
            }

            /*
             * Get tile with lowest F cost from open list.
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
                 * Get the moving costs from the current step to the
                 * neighbor.
                 */
                moveCosts = calcMoveCostsFrom(currentStep, neighbour);

                if (!open.contains(neighbour)) {
                    neighbour.parent = currentStep;
                    neighbour.moveCosts = moveCosts;
                    neighbour.calcHeuristicToDestinationAndSum(destination);
                    open.add(neighbour);
                }
            }

            iterations += 1;
        }

        ArrayList<PathTile> path = getPathTiles(start, destination);

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

    /**
     * Calculates the movecosts from one tile to this tile.
     *
     * If this tile is occupied by an item then increase the costs by factor 10 (like a wall).
     * If this tile is not occupied by an item but from has a different group the costs are slighty raised (like a bump).
     *
     * @param from The tile from which we move to this tile
     * @return The move cost from "from" to "this"
     */
    static int calcMoveCostsFrom(PathTile from, PathTile to) {

        if (to.hex.item != null) {
            return ITEM_PENALTY + from.moveCosts;
        }

        boolean entersGroup = from.hex.group == null && to.hex.group != null;
        if (entersGroup) {
            return GROUP_PENALTY + from.moveCosts;
        }

        if (to.hex.getPathDirection() != null) {
            return PATH_PENALTY + from.moveCosts;
        }

        return BASE_COSTS + from.moveCosts;
    }

    /**
     * Build the path reversibly iterating over the tiles by accessing their
     * parent tile.
     */
    private ArrayList<PathTile> getPathTiles(PathTile start, PathTile destination) {
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
        return path;
    }

    private List<PathTile> getNeighbourTiles(PathTile current) {
        List<PathTile> neighbours = new ArrayList<>();
        hexMap.getNeighbours(current.hex).forEach(hex -> neighbours.add(new PathTile(hex)));
        return neighbours;
    }

}
