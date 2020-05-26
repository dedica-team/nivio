package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.output.map.svg.HexPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A* pathfinder based on https://github.com/helfsoft/astar (no license given).
 *
 *
 *
 */
public class PathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinder.class);
    private final Set<Hex> occupied;

    public boolean debug = false;

    private final ArrayList<Tile> closed;
    private final ArrayList<Tile> open;

    /**
     * @param occupied the occupied tiles
     */
    public PathFinder(Set<Hex> occupied) {
        this.occupied = occupied;
        this.closed = new ArrayList<>();
        this.open = new ArrayList<>();
    }


    /**
     * Returns the best fitting tile in openlist to add to path based on F cost
     * of tile.
     *
     * @return The best tile for path in openlist
     */
    private Tile getBest() {

        Tile tmp = open.get(open.size() - 1);
        for (int i = open.size() - 1; i >= 1; i--) {

            // pick the lowest sum of move and heuristic
            if (open.get(i).sumCosts < tmp.sumCosts) {
                tmp = open.get(i);

            //or equal sum and lower heuristic
            } else if (open.get(i).sumCosts == tmp.sumCosts && open.get(i).heuristicCosts < tmp.heuristicCosts) {
                tmp = open.get(i);
            }

        }

        open.remove(tmp);
        //LOGGER.debug("returning best tile {} with sumCosts {} and heuristic {}", tmp, tmp.sumCosts, tmp.heuristicCosts);

        return tmp;
    }

    /**
     * Calculates the path between two tiles.
     *
     * @param startHex The start hex of the path
     * @param destHex  The destination hex of the path
     * @return A list containing all tiles of the found path
     */
    public HexPath getPath(Hex startHex, Hex destHex) {
        closed.clear();
        open.clear();
        var start = new Tile(startHex, false);
        var dst = new Tile(destHex, false);


        Tile currentStep = start;
        open.add(0, currentStep);

        float G = 0f;

        int depth = 0;
        int depthMax = 1000;

        while (true) {

            /*
             * Limit the amount of loops for better performance
             */
            if (depth >= depthMax) {
                throw new RuntimeException("Max depth exceeded.");
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
            } else {

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
                var neighbours = getNeighbours(currentStep);
                for (int i = 0; i < neighbours.size(); i++) {

                    Tile neighbour = neighbours.get(i);

                    if (neighbour.equals(dst)) {
                        neighbour.setParent(currentStep);
                        currentStep = neighbour;
                        LOGGER.info("reached  {}", currentStep.hex);
                        break;
                    }

                    if (closed.contains(neighbour)) {
                        //LOGGER.info("Already visited {}", neighbour.hex);
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

            }
            depth += 1;
        }

        /*
         * Build the path reversly iterating over the tiles by accessing their
         * parent tile.
         */
        ArrayList<Tile> path = new ArrayList<>();
        path.add(dst);
        Tile startTmp = dst;
        while (!start.equals(startTmp)) {

            if (startTmp.getParent() == null)
                break;

            startTmp = startTmp.getParent();
            if (path.contains(startTmp)) { //already contains the parent
                throw new RuntimeException("Path already contains the parent.");
            }
            path.add(startTmp);
        }

        /*
         * Reverse to get the path from start to dst.
         */
        Collections.reverse(path);

        /*
         * If no path is found return null.
         */
        if (path.isEmpty())
            return null;

        List<Hex> collect = path.stream().map(tile -> tile.hex).collect(Collectors.toList());
        return new HexPath(collect);
    }


    private List<Tile> getNeighbours(Tile current) {
        List<Tile> free = current.hex.neighbours().stream()
                .map(hex -> new Tile(hex, this.isOccupied(hex)))
                .collect(Collectors.toList());

        //LOGGER.debug("{} free tiles at {}", free.size(), current);

        return free;
    }

    private boolean isOccupied(Hex hex) {
        return occupied.contains(hex);
    }
}
