package de.bonndan.nivio.output.map.hex;

import de.bonndan.nivio.output.map.svg.HexPath;
import org.apache.commons.collections4.BidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A* pathfinder based on https://github.com/helfsoft/astar (no license given).
 */
class PathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinder.class);
    public static final int DEPTH_MAX = 2000;
    private final BidiMap<Hex, Object> hexesToItems;

    public boolean debug = false;

    private final ArrayList<Tile> closed;
    private final ArrayList<Tile> open;

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
    private Tile getBest() {

        Tile tmp = open.get(open.size() - 1);
        for (int i = open.size() - 1; i >= 0; i--) {

            // pick the lowest sum of move and heuristic
            if (open.get(i).sumCosts < tmp.sumCosts) {
                tmp = open.get(i);

                //or equal sum and lower heuristic
            } else if (open.get(i).sumCosts == tmp.sumCosts && open.get(i).heuristicCosts < tmp.heuristicCosts) {
                tmp = open.get(i);
            }

        }

        open.remove(tmp);
        LOGGER.debug("returning best tile {} with sumCosts {} and heuristic {}", tmp, tmp.sumCosts, tmp.heuristicCosts);

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
        var start = new Tile(startHex);
        var dst = new Tile(destHex);


        Tile currentStep = start;
        open.add(0, currentStep);

        float G;

        int depth = 0;

        while (true) {

            /*
             * Limit the amount of loops for better performance
             */
            if (depth >= DEPTH_MAX) {
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
            var neighbours = getNeighbours(currentStep);
            for (int i = 0; i < neighbours.size(); i++) {

                Tile neighbour = neighbours.get(i);

                if (neighbour.equals(dst)) {
                    neighbour.setParent(currentStep);
                    currentStep = neighbour;
                    LOGGER.debug("reached  {}", currentStep.hex);
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

            depth += 1;
        }

        /*
         * Build the path reversly iterating over the tiles by accessing their
         * parent tile.
         */
        ArrayList<Tile> path = new ArrayList<>();
        path.add(dst);
        Tile tileBetween = dst;
        while (!start.equals(tileBetween)) {

            if (tileBetween.getParent() == null) {
                break;
            }

            tileBetween = tileBetween.getParent();
            if (path.contains(tileBetween)) { //already contains the parent
                throw new RuntimeException("Path already contains the parent.");
            }
            if (!dst.equals(tileBetween) && !start.equals(tileBetween) && tileBetween.hex.item != null) {
                throw new RuntimeException(String.format("Path from %s to %s runs through item %s!", start, dst, tileBetween));
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

        return Optional.of(new HexPath(path.stream().map(tile -> tile.hex).collect(Collectors.toList())));
    }


    private List<Tile> getNeighbours(Tile current) {

        return current.hex.neighbours().stream()
                .map(hex -> {
                    Object val = hexesToItems.get(hex);
                    if (val != null) {
                        hex = hexesToItems.inverseBidiMap().get(val);
                    }
                    return new Tile(hex);
                })
                .collect(Collectors.toList());
    }

}
